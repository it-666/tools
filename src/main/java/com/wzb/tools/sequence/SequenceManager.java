package com.wzb.tools.sequence;

import org.springframework.stereotype.Service;

/**
 * the interface of sequence mannager
 */
@Service
public interface SequenceManager {
    /**
     * create a sequence
     *
     * @param sequenceName the name of sequence
     */
    void create(String sequenceName);

    /**
     * get next id in long format
     *
     * @param sequenceName the name of sequence
     * @return next id
     */
    long nextId(String sequenceName);

    /**
     * get next id in string format
     *
     * @param sequenceName the name of sequence
     * @return next id
     */
    default String nextStringId(String sequenceName) {
        return String.valueOf(nextId(sequenceName));
    }
}
