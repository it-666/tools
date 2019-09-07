package com.wzb.tools.sequence.impl;

import com.wzb.tools.sequence.Sequence;
import com.wzb.tools.sequence.SequenceFactory;
import com.wzb.tools.sequence.SequenceManager;
import com.wzb.tools.worker.Worker;
import com.wzb.tools.worker.impl.DefaultWorkerImpl;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * the implement of sequence manager
 */
@Slf4j
public class SequenceManagerImpl implements SequenceManager {
    /**
     * the sleep time in reserved thread in millisecond (1 minute)
     */
    private static final int RESERVED_SLEEP_IN_MILLS = 1000 * 60;
    /**
     * the sleep time in restore thread in millisecond
     */
    private static final int RESTORE_SLEEP_IN_MILLS = RESERVED_SLEEP_IN_MILLS * 2;
    /**
     * the worker id interface
     */
    @Setter
    private Worker worker = new DefaultWorkerImpl();
    /**
     * the factory for sequence
     */
    @Setter
    private SequenceFactory factory = new SequenceFactory();
    /**
     * the contain for sequence
     */
    private Map<String, Sequence> sequenceMap = new ConcurrentHashMap<>(32);
    /**
     * the thread for reserved worker id
     */
    private Thread reservedWorkerIdThread = new Thread(() -> {
        while (true) {
            for (Iterator<Map.Entry<String, Sequence>> itr = sequenceMap.entrySet().iterator(); itr.hasNext(); ) {
                Map.Entry<String, Sequence> item = itr.next();
                if (worker.reservedWorkerId(item.getKey())) {
                    continue;
                }
                log.info("the sequence {} reserved work id failed, remove it", item.getKey());
                itr.remove();
            }
            try {
                Thread.sleep(RESERVED_SLEEP_IN_MILLS);
            } catch (Exception e) {
                log.info("the reserved thread is interrupted");
                break;
            }
        }
    });
    /**
     * the thread for restore worker id
     */
    private Thread reserveWorkerIdThread = new Thread(() -> {
        while (true) {
            sequenceMap.forEach((k, v) -> worker.reservedWorkerId(k));
            try {
                Thread.sleep(RESTORE_SLEEP_IN_MILLS);
            } catch (Exception e) {
                log.info("the reserved thread is interrupted");
                break;
            }
        }
    });

    /**
     * destroy resources when the process exits
     */
    @PostConstruct
    public void init() {
        reservedWorkerIdThread.setName("SeqReserveThread");
        reserveWorkerIdThread.setName("SeqReserveThread");
        reservedWorkerIdThread.start();
        reserveWorkerIdThread.start();
        log.info("sequence manager thread stared.");
    }

    /**
     * destroy resources when the process exits
     */
    @PreDestroy
    public void destroy() {
        try {
            // stop the thread
            reservedWorkerIdThread.interrupt();
            reserveWorkerIdThread.interrupt();
            reservedWorkerIdThread.join();
            reserveWorkerIdThread.join();
        } catch (InterruptedException e) {
            // do nothing for now
        }
        // give back work id
        sequenceMap.forEach((k, v) -> worker.giveBackWorkerId(k));
        sequenceMap.clear();
        log.info("sequence manager stopped.");
    }

    /**
     * create a sequence
     *
     * @param sequenceName the name of sequence
     */
    public void create(String sequenceName) {
        Sequence obj = sequenceMap.get(sequenceName);
        if (obj != null) {
            return;
        }
        createImpl(sequenceName);
    }

    /**
     * create a sequence(thread safe)
     *
     * @param sequenceName the name of sequence
     * @return
     */
    @Synchronized
    private Sequence createImpl(String sequenceName) {
        Sequence obj = sequenceMap.get(sequenceName);
        if (obj != null) {
            return obj;
        }
        int workId = worker.getWorkerId(sequenceName, factory.getMaxWordId());//use name as work place
        log.debug("create sequence {} with worker id {}", sequenceName, workId);
        obj = factory.createSequences(workId);
        sequenceMap.put(sequenceName, obj);
        return obj;
    }

    /**
     * get next id in long format
     *
     * @param sequenceName the ame of sequence
     * @return next id
     */
    public long nextId(String sequenceName) {
        Sequence obj = sequenceMap.get(sequenceName);
        if (obj == null) {
            obj = createImpl(sequenceName);
        }
        return obj.getNext();
    }
}
