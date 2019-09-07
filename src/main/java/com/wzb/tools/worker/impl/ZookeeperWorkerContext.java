package com.wzb.tools.worker.impl;

import com.wzb.tools.pool.CuratorClientPool;
import lombok.Getter;
import lombok.Setter;
import org.apache.curator.framework.CuratorFramework;

/**
 * the runtime context for worker id sequence based on zookeeper
 */
@Setter
@Getter
public class ZookeeperWorkerContext {
    /**
     * the namespace of worker id
     */
    @Setter
    private String namespace;
    /**
     * the zookeeper connect spring
     */
    @Setter
    private String connect;
    /**
     * the client for zookeeper
     */
    @Setter
    private CuratorClientPool pool;

    /**
     * get connect client for curator
     *
     * @return
     */
    public CuratorFramework getClient() {
        return pool.getClient(namespace, connect);
    }
}
