package com.xiaoju.uemc.tinyid.base.entity;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author du_imba
 *         nextId: (currentId, maxId]
 *
 * 分段id
 *
 */
public class SegmentId {
    /**
     * 最大id
     * 不允许超过
     */
    private long maxId;
    /**
     * 阈值，达到某个值就去加载下一个号段id
     */
    private long loadingId;
    /**
     * 当前id
     */
    private AtomicLong currentId;
    /**
     * increment by
     * 步长
     * 每次增长固定步长
     */
    private int delta;
    /**
     * mod num
     * 余数
     */
    private int remainder;

    /**
     * 是否初始化
     */
    private volatile boolean isInit;

    /**
     * 这个方法主要为了1,4,7,10...这种序列准备的
     * 设置好初始值之后，会以delta的方式递增，保证无论开始id是多少都能生成正确的序列
     * 如当前是号段是(1000,2000]，delta=3, remainder=0，则经过这个方法后，currentId会先递增到1002,之后每次增加delta
     * 因为currentId会先递增，所以会浪费一个id，所以做了一次减delta的操作，实际currentId会从999开始增，第一个id还是1002
     */
    public void init() {
        if (isInit) {
            return;
        }
        synchronized (this) {
            if (isInit) {
                return;
            }
            long id = currentId.get();
            // 如果当然id模步长等于余数
            // 则初始化完成
            if (id % delta == remainder) {
                isInit = true;
                return;
            }
            // 调整currentId  保证可以模delta
            for (int i = 0; i <= delta; i++) {
                id = currentId.incrementAndGet();
                if (id % delta == remainder) {
                    // 避免浪费 减掉系统自己占用的一个id
                    currentId.addAndGet(0 - delta);
                    isInit = true;
                    return;
                }
            }
        }
    }

    /**
     * 获取下个id, 带上状态编码
     * 状态编码为了告诉上层
     *      loading  -->  这里id库存不够了啊， 要去获取下个号段的id啦
     *      over     -->  当期号段id用完， 要用下一号段了
     * @return
     */
    public Result nextId() {
        init();
        long id = currentId.addAndGet(delta);
        if (id > maxId) {
            return new Result(ResultCode.OVER, id);
        }
        if (id >= loadingId) {
            //
            return new Result(ResultCode.LOADING, id);
        }
        return new Result(ResultCode.NORMAL, id);
    }

    /**
     * 是否可用
     * 小于设定的最大值， 即可用
     * @return
     */
    public boolean useful() {
        return currentId.get() <= maxId;
    }

    // ========================  getter setter ToString 分割线 ========================

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public long getLoadingId() {
        return loadingId;
    }

    public void setLoadingId(long loadingId) {
        this.loadingId = loadingId;
    }

    public AtomicLong getCurrentId() {
        return currentId;
    }

    public void setCurrentId(AtomicLong currentId) {
        this.currentId = currentId;
    }

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public int getRemainder() {
        return remainder;
    }

    public void setRemainder(int remainder) {
        this.remainder = remainder;
    }

    @Override
    public String toString() {
        return "[maxId=" + maxId + ",loadingId=" + loadingId + ",currentId=" + currentId + ",delta=" + delta + ",remainder=" + remainder + "]";
    }
}
