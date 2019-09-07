package com.wzb.tools.sequence.impl;

import com.wzb.tools.sequence.Sequence;
import lombok.Setter;
import lombok.Synchronized;

public class SnowflakeSequenceImpl implements Sequence {
    /**
     * 起始的时间戳（2018-10-08 00-00-00）
     */
    private final static long BASE_EPOCH = 1538928000000L;
    /**
     * 序列在id中占的位数
     */
    private static final int SEQUENCE_BITS = 12;
    /**
     * 生成序列的掩码，这里为 4095
     */
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);
    /**
     * worker id 所占的位数
     */
    private static final int WORKER_ID_BITS = 10;
    /**
     * 支持的最大work id，结果是 1023
     */
    private static final int MAX_WORKER_ID = (int) (-1L ^ (-1L << WORKER_ID_BITS));
    /**
     * work id 向左移12位
     */
    private static final int WORKER_ID_SHIFT = SEQUENCE_BITS;
    /**
     * 时间截向左移22位 （5+5+12）
     */
    private static final int TIMESTAMP_LEFT_SHIFT = WORKER_ID_SHIFT + WORKER_ID_BITS;
    /**
     * 毫秒内序列（0~4095）
     */
    private long millsIndex;
    /**
     * work id
     */
    @Setter
    private long workerId;
    /**
     * 上次生成ID的时间戳
     */
    private long lastTimestamp;

    /**
     * 构造函数（私有）
     */
    private SnowflakeSequenceImpl() {
    }

    /**
     * 工厂方法
     *
     * @param workerId the worker id
     * @return
     */
    public static SnowflakeSequenceImpl build(int workerId) {
        if (workerId > MAX_WORKER_ID) {
            throw new RuntimeException("the worker id is too large,id is " + workerId);
        }
        SnowflakeSequenceImpl impl = new SnowflakeSequenceImpl();
        impl.setWorkerId(workerId);
        return impl;
    }

    /**
     * @return maximum work id supported
     */
    public static int getMaxWorkerId() {
        return MAX_WORKER_ID;
    }

    /**
     * 获得下一个ID （线程安全）
     *
     * @return id
     */
    @Synchronized
    public long getNext() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            String message = String.format("clock moved backwards,refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp);
            throw new RuntimeException(message);
        }
        if (lastTimestamp == timestamp) { // 毫秒一致，序列处理
            millsIndex = (millsIndex + 1) & SEQUENCE_MASK;
            if (millsIndex == 0) { //益处等待
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else { // 毫秒不一致，毫秒重新生成
            millsIndex = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - BASE_EPOCH) << TIMESTAMP_LEFT_SHIFT) | (workerId << WORKER_ID_SHIFT) | millsIndex;
    }

    /**
     * 阻塞下一毫秒，直到下一毫秒
     *
     * @param lastTimestamp 上次生成id 的时间戳
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间（毫秒）
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
}
