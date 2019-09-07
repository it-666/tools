package com.wzb.tools.worker.impl;

import lombok.Data;

/**
 * the model for worker
 */
@Data
public class WorkerModel {
    /**
     * the name of worker
     */
    private String name;
    /**
     * the id of worker
     */
    private int id;
    /**
     * exception times
     */
    private int exceptionTimes;
    /**
     * current version
     */
    private long version;

    /**
     * get worker id node path
     *
     * @param basePath base path
     * @return path
     */
    public String getNodePath(String basePath) {
        return basePath + "/" + id;
    }
}
