package com.wzb.tools.worker.impl;

import com.wzb.tools.cleanup.CuratorLockCleaner;
import com.wzb.tools.id.CuratorClient;
import com.wzb.tools.worker.Worker;
import lombok.Cleanup;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ZookeeperWorkerImpl implements Worker {
    /**
     * serial path
     */
    private static final String SERIAL_PATH = "serial";
    /**
     * the max times attempt to get lock
     */
    private static final int LOCK_MAX_TIMES = 10;
    /**
     * the min times attempt to get lock
     */
    private static final int LOCK_MIN_TIMES = 1;
    /**
     * max version fall behind
     */
    private static final long MAX_VERSION_FALL_BEHIND = 60;
    /**
     * the curator client utils
     */
    private ThreadLocal<CuratorClient> clientThreadLocal = ThreadLocal.withInitial(CuratorClient::new);
    /**
     * the contain map :worker place to worker id
     */
    private Map<String, WorkerModel> workerIdDict = new ConcurrentHashMap<>(32);
    /**
     * the runtime context
     */
    @Setter
    private ZookeeperWorkerContext context;

    /**
     * 设置客户端信息
     *
     * @param workPlace 工作地点（zookeeper 节点路径）
     * @return 客户端
     */
    private CuratorClient setupClient(final String workPlace) {
        CuratorClient client = clientThreadLocal.get();
        client.setClient(context.getClient());
        client.setBasePath(workPlace);
        return client;
    }

    /**
     * @param workPlace work place(zookeeper node path)
     * @param maxWorkId the maximum work id
     * @return worker id
     */
    @SneakyThrows
    public int getWorkerId(final String workPlace, int maxWorkId) {
        WorkerModel worker = workerIdDict.get(workPlace);
        if (worker != null) {
            return worker.getId();
        }
        // generator workid
        return generatorWorkerId(workPlace, maxWorkId);
    }

    /**
     * @param workPlace   work place(zookeeper node path)
     * @param maxWorkerId the maximum work id
     * @return worker id
     */
    @Synchronized
    @SneakyThrows
    private int generatorWorkerId(final String workPlace, int maxWorkerId) {
        WorkerModel worker = workerIdDict.get(workPlace);
        if (worker != null) {
            return worker.getId();
        }
        CuratorClient client = setupClient(workPlace);
        client.createLockNode();// use write lock
        @Cleanup CuratorLockCleaner mutex = client.getWriteLock(LOCK_MAX_TIMES);
        // get config node info
        client.createNode(SERIAL_PATH, CreateMode.PERSISTENT, 0);
        List<String> usedSerial = client.getChildNode(SERIAL_PATH);
        if (CollectionUtils.isEmpty(usedSerial)) { // no node,there is first node
            return createSerialNode(workPlace, 0);
        }
        List<Integer> serials = usedSerial.stream().map(Integer::parseInt).sorted().collect(Collectors.toList());
        for (int seq = 0; seq < maxWorkerId; seq++) { // use minimal unused seq as work id
            if (seq > (serials.size() - 1)) {
                return createSerialNode(workPlace, seq);
            }
            if (serials.get(seq) > seq) { // the child seq is not continuous,use it!
                return createSerialNode(workPlace, seq);
            }
        }
        throw new RuntimeException("no work id left to use, work place " + workPlace);
    }

    /**
     * create serial node
     *
     * @param workPlace work place (zookeeper node path)
     * @param workerId  the work id
     * @return worker id
     */
    private int createSerialNode(final String workPlace, int workerId) {
        log.debug("generator work id: {} = {}", workPlace, workerId);
        WorkerModel worker = new WorkerModel();
        worker.setName(workPlace);
        worker.setId(workerId);
        worker.setExceptionTimes(0);
        CuratorClient client = clientThreadLocal.get();
        worker.setVersion(client.getLongData(SERIAL_PATH));
        workerIdDict.put(workPlace, worker);
        // create node on zookeeper
        String node = worker.getNodePath(SERIAL_PATH);
        client.createNode(node, CreateMode.PERSISTENT, worker.getVersion());
        // create instance node to desc which client use this serial
        client.createInstanceNode(node);
        return worker.getId();
    }

    /**
     * give back work id used when process exits
     *
     * @param workPlace work place
     */
    public void giveBackWorkerId(final String workPlace) {
        try {
            WorkerModel worker = workerIdDict.get(workPlace);
            if (worker == null) {
                return;
            }
            log.debug("giveback worker id: {} = {}", workPlace, worker.getId());
            CuratorClient client = setupClient(workPlace);
            client.deleteNode(worker.getNodePath(SERIAL_PATH));
            workerIdDict.remove(workPlace);
        } catch (Exception e) {
            // doing nothing for now
        }
    }

    /**
     * reserved work id that are in use
     *
     * @param workPlace work place
     * @return success or not
     */
    public boolean reservedWorkerId(final String workPlace) {
        WorkerModel worker = workerIdDict.get(workPlace);
        if (worker == null) {
            return false;
        }
        try {
            CuratorClient client = setupClient(workPlace);
            String serialNode = worker.getNodePath(SERIAL_PATH);
            if (!client.checkExists(serialNode)) { // the node has been deleted
                giveBackWorkerId(workPlace);
                return false;
            }
            client.setRawData(serialNode, client.getRawData(SERIAL_PATH));
            worker.setExceptionTimes(0);
            return true;
        } catch (Exception e) {
            int exceptionTimes = worker.getExceptionTimes() + 1;
            if (exceptionTimes < MAX_VERSION_FALL_BEHIND) {
                worker.setExceptionTimes(exceptionTimes);
                return true;
            }
            giveBackWorkerId(workPlace);
            return false;
        }
    }

    /**
     * restore work ids that are no longer in use
     *
     * @param workPlace work place
     */
    public void restoresWorkerId(final String workPlace) {
        try {
            // first check the place is in dict
            WorkerModel worker = workerIdDict.get(workPlace);
            if (worker == null) {
                return;
            }
            CuratorClient client = setupClient(workPlace);
            //if the node version is already update , just follow
            long nodeVersion = client.getLongData(SERIAL_PATH);
            if (nodeVersion > worker.getVersion()) {
                worker.setVersion(nodeVersion);
                return;
            }
            @Cleanup CuratorLockCleaner mutex = client.tryGetWriteLock(LOCK_MIN_TIMES);
            //can't get lock ,return is ok (other instance working)
            if (mutex == null) {
                return;
            }
            // do the restore
            restoresWorkerIdImpl(worker);
        } catch (Exception e) {
            //do nothing for now
        }
    }

    /**
     * restore work ids that are no longer in use
     *
     * @param worker worker model
     */
    private void restoresWorkerIdImpl(WorkerModel worker) {
        CuratorClient client = clientThreadLocal.get();
        // get node version again
        long nodeVersion = client.getLongData(SERIAL_PATH);
        if (nodeVersion > worker.getVersion()) {
            worker.setVersion(nodeVersion);
            return;
        }
        // update node version
        nodeVersion = nodeVersion + 1;
        // get child
        List<String> childNodes = client.getChildNode(SERIAL_PATH);
        if (CollectionUtils.isEmpty(childNodes)) {
            return;
        }
        //delete the child node which version is far fall behind.
        for (String node : childNodes) {
            String serialPath = SERIAL_PATH + "/" + node;
            long childVersion = client.getLongData(serialPath);
            if (nodeVersion - childVersion >= MAX_VERSION_FALL_BEHIND) {
                log.info("restore worker id: {} = {}", worker.getName(), node);
                client.deleteNode(serialPath);
            }
        }
        //finally update main version local & remote
        worker.setVersion(nodeVersion);
        client.setLongData(SERIAL_PATH, nodeVersion);
    }
}
