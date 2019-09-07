package com.wzb.tools.cleanup;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.locks.InterProcessLock;

/**
 * curator lock's clean up wrapper
 */
@AllArgsConstructor
@Slf4j
public class CuratorLockCleaner {
    /**
     * the lock
     */
    @Setter
    private InterProcessLock lock;

    /**
     * release lock
     */
    public void close() {
        if (lock == null || !lock.isAcquiredInThisProcess()) {
            return;
        }
        try {
            lock.release();
        } catch (Exception e) {
            log.info("release one lock failed");
        }
    }

}
