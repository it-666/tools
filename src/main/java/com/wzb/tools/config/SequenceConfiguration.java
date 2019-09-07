package com.wzb.tools.config;

import com.wzb.tools.pool.CuratorClientPool;
import com.wzb.tools.sequence.FrameworkIdGenerator;
import com.wzb.tools.sequence.SequenceManager;
import com.wzb.tools.sequence.impl.SequenceManagerImpl;
import com.wzb.tools.worker.Worker;
import com.wzb.tools.worker.impl.DefaultWorkerImpl;
import com.wzb.tools.worker.impl.ZookeeperWorkerContext;
import com.wzb.tools.worker.impl.ZookeeperWorkerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * the configuration for sequence
 */
@Slf4j
@Configuration
public class SequenceConfiguration {
    /**
     * the bean for curator client pool
     *
     * @return the pool
     */
    @Bean
    public CuratorClientPool curatorClientPool() {
        return new CuratorClientPool();
    }

    /**
     * the properties for work id generator based on zookeeper
     *
     * @return the properties
     */
    @Bean
    @ConditionalOnExpression("'${ad.sequence.registry.server-list:}'.length() > 0")
    public SequenceProperties sequenceRegistryProperties() {
        return new SequenceProperties();
    }

    /**
     * the context for work id generator based on zookeeper
     *
     * @param pool
     * @param properties
     * @return the context
     */
    @Bean
    @ConditionalOnBean(SequenceProperties.class)
    public ZookeeperWorkerContext zookeeperWorkerContext(CuratorClientPool pool, SequenceProperties properties) {
        log.debug("registry properties: {}", properties);
        ZookeeperWorkerContext context = new ZookeeperWorkerContext();
        context.setPool(pool);
        context.setNamespace(properties.getNamespace());
        context.setConnect(properties.getServerList());
        return context;
    }

    /**
     * the worker id generator
     *
     * @param context
     * @return the worker
     */
    @Bean
    @ConditionalOnBean(ZookeeperWorkerContext.class)
    public Worker zookeeperWorker(ZookeeperWorkerContext context) {
        ZookeeperWorkerImpl worker = new ZookeeperWorkerImpl();
        worker.setContext(context);
        return worker;
    }

    /**
     * the worker id generator
     *
     * @return the worker
     */
    @Bean
    @ConditionalOnMissingBean
    public Worker defaultWorker() {
        return new DefaultWorkerImpl();
    }

    /**
     * the sequence manager
     *
     * @param worker
     * @return the sequence manager
     */
    @Bean
    public SequenceManager sequenceManager(Worker worker) {
        SequenceManagerImpl manager = new SequenceManagerImpl();
        manager.setWorker(worker);
        return manager;
    }

    /**
     * the framework id generator
     *
     * @param sequenceManager
     * @return the framework id generator
     */
    @Bean
    public FrameworkIdGenerator frameworkIdGenerator(SequenceManager sequenceManager) {
        FrameworkIdGenerator generator = new FrameworkIdGenerator();
        generator.setSequenceManager(sequenceManager);
        return generator;
    }
}
