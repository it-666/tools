package com.wzb.tools.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * the properties for sequence
 */
@Data
@ConfigurationProperties("ad.sequence.registry")
public class SequenceProperties {
    /**
     * 注册中心zookeeper地址，多个zk地址以“，”分割
     */
    private String serverList;
    /**
     * 命名空间，即zookeeper基础目录
     */
    private String namespace;
}
