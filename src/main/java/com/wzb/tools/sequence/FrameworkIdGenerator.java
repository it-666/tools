package com.wzb.tools.sequence;

import com.wzb.tools.id.IdGenerator;

/**
 * the id generator for framework
 */
public class FrameworkIdGenerator extends IdGenerator {
    /**
     * the sequence name
     */
    private static final String SEQUENCE_NAME = "FRAMEWORK_SET";

    /**
     * 构造函数
     */
    public FrameworkIdGenerator() {
        super(SEQUENCE_NAME);
    }
}
