package com.wzb.tools.worker;

/**
 * the interface for worker
 */
public interface Worker {
    /**
     * @param workPlace   work place
     * @param maxWorkerId the maximum work id
     * @return worker id
     */
    default int getWorkerId(final String workPlace, int maxWorkerId) {
        return 0;
    }

    /**
     * gice back work id whem process exits(for reuse)
     *
     * @param workPlace work place
     */
    default void giveBackWorkerId(final String workPlace) {
    }

    /**
     * reserved work id that are in use
     *
     * @param workPlace work place
     * @return success or not
     */
    default boolean reservedWorkerId(final String workPlace) {
        return true;
    }

    /**
     * restore work ids that are no longer in use
     *
     * @param workPlace work place
     */
    default void restoresWorkerId(final String workPlace) {
    }
}
