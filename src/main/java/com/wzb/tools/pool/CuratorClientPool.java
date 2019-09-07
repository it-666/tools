package com.wzb.tools.pool;

import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * the pool for curator client
 */
@Slf4j
public class CuratorClientPool {
    /**
     * retry policy
     */
    private RetryPolicy retryPolicy = new ExponentialBackoffRetry(10, 5);
    /**
     * the pool for CuratorFramework
     */
    private Map<String, CuratorFramework> pool = new ConcurrentHashMap<>(16);

    /**
     * get curator client by namespace and connect string
     *
     * @param namespace the pre-defined namespace
     * @param connect   connect string
     * @return client
     */
    public CuratorFramework getClient(String namespace, String connect) {
        String key = getKey(namespace, connect);
        CuratorFramework client = pool.get(key);
        if (client == null) {
            client = newClient(namespace, connect);
            client.start();
            waitUntilStarted(client, connect);
            pool.put(key, client);
        }
        return client;
    }

    /**
     * create curator client by namespace and connect string
     *
     * @param namespace the pre-defined namespace
     * @param connect   connect string
     * @return client
     */
    @Synchronized
    private CuratorFramework newClient(String namespace, String connect) {
        String key = getKey(namespace, connect);
        CuratorFramework client = pool.get(key);
        if (client != null) {
            return client;
        }
        return CuratorFrameworkFactory.builder().namespace(namespace).connectString(connect).retryPolicy(retryPolicy).build();
    }

    /**
     * @param namespace the pre-defined namespace
     * @param connect   connect string
     * @return map key
     */
    private String getKey(String namespace, String connect) {
        return namespace + "@" + connect;
    }

    /**
     * wait unit client stared
     *
     * @param client  curator client
     * @param connect connect string
     */
    @SneakyThrows
    private void waitUntilStarted(CuratorFramework client, String connect) {
        for (int index = 0; index < 30; index++) {
            if (client.getState() == CuratorFrameworkState.STARTED) {
                break;
            }
            if (index % 5 == 0) {
                log.debug("wait curator[{}] client start", connect);
            }
            Thread.sleep(100);
        }
        if (client.getState() != CuratorFrameworkState.STARTED) {
            throw new RuntimeException("can't establish an connection to " + connect);
        }
    }

    /**
     * clean curator client resource
     */
    @PreDestroy
    public void destroy() {
        pool.forEach((k, v) -> v.close());
        pool.clear();
        log.info("the client pool destroyed");
    }
}
