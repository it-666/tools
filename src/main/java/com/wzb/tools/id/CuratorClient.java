package com.wzb.tools.id;

import com.wzb.tools.cleanup.CuratorLockCleaner;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.springframework.lang.NonNull;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * curator client tool
 */
@Slf4j
public class CuratorClient {
    /**
     * lock path
     */
    private static final String DEFAULT_LOCK_PATH = "latch";
    /**
     * instance path
     */
    private static final String DEFAULT_INSTANCE_PATH = "instance";
    /**
     * the curator client
     */
    @Setter
    private CuratorFramework client;
    /**
     * the work base path
     */
    private String basePath;

    /**
     * get base path
     *
     * @param basePath zookeeper base node path
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath.startsWith("/") ? basePath : "/" + basePath;
    }

    /**
     * get base path
     *
     * @param path the relative path
     * @return
     */
    private String getRealPath(String path) {
        if (Objects.nonNull(path)) {
            path = path.startsWith("/") ? path : "/" + path;
            return basePath + path;
        }
        return basePath;
    }

    /**
     * get instance path
     *
     * @return instance path
     */
    @SneakyThrows
    public String getInstancePath() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        return "/" + (pid + "@" + ip);
    }

    /**
     * create zookeeper node if exists or not
     *
     * @param path zookeeper mode path
     * @return true: exist , false: not exist
     */
    @SneakyThrows
    public boolean checkExists(String path) {
        return null != client.checkExists().forPath(getRealPath(path));
    }

    /**
     * create zookeeper node
     *
     * @param path       zookeeper mode path
     * @param createMode the create node of path
     * @param data       the the data
     */
    public void createNode(String path, CreateMode createMode, long data) {
        createNode(path, createMode, String.valueOf(data));
    }

    /**
     * create zookeeper node
     *
     * @param path       zookeeper mode path
     * @param createMode the create node of path
     * @param data       the the data
     */
    public void createNode(String path, CreateMode createMode, String data) {
        createNode(path, createMode, data.getBytes());
    }

    /**
     * create zookeeper node
     *
     * @param path       zookeeper mode path
     * @param createMode the create node of path
     * @param data       the the data
     */
    @SneakyThrows
    public void createNode(String path, CreateMode createMode, byte[] data) {
        if (checkExists(path)) {
            return;
        }
        try {
            client.create().creatingParentsIfNeeded().withMode(createMode).forPath(getRealPath(path), data);
        } catch (Exception e) {
            // do nothing for now
        }
    }

    /**
     * create zookeeper node
     *
     * @param path       zookeeper mode path
     * @param createMode the create node of path
     */
    @SneakyThrows
    public void createNode(String path, CreateMode createMode) {
        if (checkExists(path)) {
            return;
        }
        try {
            client.create().creatingParentsIfNeeded().withMode(createMode).forPath(getRealPath(path));
        } catch (Exception e) {
            // do nothing for now
        }
    }

    /**
     * create zookeeper node
     *
     * @param path zookeeper mode path
     */
    @SneakyThrows
    public void deleteNode(String path) {
        if (!checkExists(path)) {
            return;
        }
        try {
            client.delete().deletingChildrenIfNeeded().forPath(getRealPath(path));
        } catch (KeeperException.NoNodeException e) {
            // do nothing for now
        }
    }

    /**
     * create zookeeper lock node
     */
    public void createLockNode() {
        createNode(DEFAULT_LOCK_PATH, CreateMode.PERSISTENT);
    }

    /**
     * get write lock of lock node
     *
     * @param maxAcquireTimes the max times trying to acquire lock
     * @return the locked mute
     */
    public CuratorLockCleaner getWriteLock(int maxAcquireTimes) {
        InterProcessMutex writeLock = new InterProcessReadWriteLock(client, getRealPath(DEFAULT_LOCK_PATH)).writeLock();
        InterProcessMutex mutex = acquireMutex(writeLock, maxAcquireTimes);
        if (mutex != null) {
            return new CuratorLockCleaner(mutex);
        }
        throw new RuntimeException("get write lock failed,lock node " + getRealPath(DEFAULT_LOCK_PATH));
    }

    /**
     * get write lock of lock node
     *
     * @param maxAcquireTimes the max times trying to acquire lock
     * @return the locked mute
     */
    public CuratorLockCleaner tryGetWriteLock(int maxAcquireTimes) {
        InterProcessMutex writeLock = new InterProcessReadWriteLock(client, getRealPath(DEFAULT_LOCK_PATH)).writeLock();
        InterProcessMutex mutex = acquireMutex(writeLock, maxAcquireTimes);
        return (mutex != null ? new CuratorLockCleaner(mutex) : null);
    }

    /**
     * get write lock of lock node
     *
     * @param maxAcquireTimes the max times trying to acquire lock
     * @return the locked mute
     */
    public CuratorLockCleaner getReadLock(int maxAcquireTimes) {
        InterProcessMutex writeLock = new InterProcessReadWriteLock(client, getRealPath(DEFAULT_LOCK_PATH)).readLock();
        InterProcessMutex mutex = acquireMutex(writeLock, maxAcquireTimes);
        if (mutex != null) {
            return new CuratorLockCleaner(mutex);
        }
        throw new RuntimeException("get write lock failed,lock node " + getRealPath(DEFAULT_LOCK_PATH));
    }

    /**
     * get write lock of lock node
     *
     * @param maxAcquireTimes the max times trying to acquire lock
     * @return the locked mute
     */
    public CuratorLockCleaner tryGetReadLock(int maxAcquireTimes) {
        InterProcessMutex writeLock = new InterProcessReadWriteLock(client, getRealPath(DEFAULT_LOCK_PATH)).readLock();
        InterProcessMutex mutex = acquireMutex(writeLock, maxAcquireTimes);
        return (mutex != null ? new CuratorLockCleaner(mutex) : null);
    }

    /**
     * acquire lock
     *
     * @param mutex           the mute try to lock
     * @param maxAcquireTimes the max times trying to acquire lock
     * @return the locked mute
     */
    private InterProcessMutex acquireMutex(InterProcessMutex mutex, int maxAcquireTimes) {
        for (int time = 0; time < maxAcquireTimes; time++) {
            try {
                if (mutex.acquire(1000, TimeUnit.MILLISECONDS)) {
                    return mutex;
                }
            } catch (Exception e) {
                log.warn("try acquire lock failed,lock node: {}, attempt time: {}", getRealPath(DEFAULT_LOCK_PATH), time);
            }
        }
        return null;
    }

    /**
     * create zookeeper instance node
     */
    public void createInstanceNode() {
        createNode(DEFAULT_INSTANCE_PATH + getInstancePath(), CreateMode.PERSISTENT);//ephemeral
    }

    /**
     * create zookeeper instance node
     */
    public void createInstanceNode(@NonNull String path) {
        createNode(DEFAULT_INSTANCE_PATH + getInstancePath(), CreateMode.PERSISTENT);//ephemeral
    }

    /**
     * get child
     *
     * @param path
     * @return
     */
    @SneakyThrows
    public List<String> getChildNode(String path) {
        return client.getChildren().forPath(getRealPath(path));
    }

    /**
     * get data of path
     *
     * @param path
     * @return
     */
    public long getLongData(String path) {
        String value = getStringData(path);
        if (value == null) {
            return 0;
        }
        return Long.valueOf(value);
    }

    /**
     * set data of path
     *
     * @param path
     * @param data
     * @return
     */
    public boolean setLongData(String path, long data) {
        return setStringData(path, String.valueOf(data));
    }

    /**
     * get data of path
     *
     * @param path
     * @return
     */
    public String getStringData(String path) {
        byte[] b = getRawData(path);
        if (b == null) {
            return null;
        }
        return new String(b);
    }

    /**
     * set data of path
     *
     * @param path
     * @param data
     * @return
     */
    public boolean setStringData(String path, String data) {
        return setRawData(path, data.getBytes());
    }

    /**
     * get data of path
     *
     * @param path
     * @return
     */
    @SneakyThrows
    public byte[] getRawData(String path) {
        return client.getData().forPath(getRealPath(path));
    }

    /**
     * set data of path
     *
     * @param path
     * @param data
     * @return
     */
    public boolean setRawData(String path, byte[] data) {
        try {
            client.setData().forPath(getRealPath(path));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
