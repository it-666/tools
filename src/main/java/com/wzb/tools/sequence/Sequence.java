package com.wzb.tools.sequence;

/**
 * the interface for Sequence
 */
public interface Sequence {
    /**
     * get sequence's next id
     *
     * @return next id
     */
    default long getNext() {
        return 0;
    }
}
