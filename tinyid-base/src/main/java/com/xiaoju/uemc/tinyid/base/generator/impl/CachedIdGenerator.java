package com.xiaoju.uemc.tinyid.base.generator.impl;

import com.xiaoju.uemc.tinyid.base.entity.Result;
import com.xiaoju.uemc.tinyid.base.entity.ResultCode;
import com.xiaoju.uemc.tinyid.base.entity.SegmentId;
import com.xiaoju.uemc.tinyid.base.exception.TinyIdSysException;
import com.xiaoju.uemc.tinyid.base.generator.IdGenerator;
import com.xiaoju.uemc.tinyid.base.service.SegmentIdService;
import com.xiaoju.uemc.tinyid.base.util.NamedThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author du_imba
 * 带缓存的ID生成器
 */
public class CachedIdGenerator implements IdGenerator {
    protected String bizType;
    protected SegmentIdService segmentIdService;

    /**
     * 当前号段
     */
    protected volatile SegmentId current;

    /**
     * 下一个号段
     * （不直接使用，只是提前获取，错峰）
     */
    protected volatile SegmentId next;


    public CachedIdGenerator(String bizType, SegmentIdService segmentIdService) {
        this.bizType = bizType;
        this.segmentIdService = segmentIdService;
        loadCurrent();
    }

    /**
     * 加载 当前号段(current)
     * 1. 实例化时调用
     * 2. 获取id 且 当前号段(current)为空
     * 3. 当前号段用完了 (currentId > maxId)
     */
    public synchronized void loadCurrent() {
        // 第一次调用 或 当前号段不可用
        if (current == null || !current.useful()) {
            if (next == null) {
                // 第一次调用
                SegmentId segmentId = querySegmentId();
                this.current = segmentId;
            } else {
                // 使用下一号段
                current = next;
                next = null;
            }
        }
    }

    /**
     * 获取号段id
     * @throws TinyIdSysException 获取不到抛出异常
     * @return
     */
    private SegmentId querySegmentId() {
        String message = null;
        try {
            SegmentId segmentId = segmentIdService.getNextSegmentId(bizType);
            if (segmentId != null) {
                return segmentId;
            }
        } catch (Exception e) {
            message = e.getMessage();
        }
        throw new TinyIdSysException("error query segmentId: " + message);
    }

    /**
     * 当前是否正在加载下一个号段
     * 状态值
     */
    private volatile boolean isLoadingNext;
    /**
     * 同步锁
     * 加载下一号段使用
     */
    private Object lock = new Object();
    /**
     * 单线程池
     * 用于异步获取下一个号段
     */
    private ExecutorService executorService = Executors.newSingleThreadExecutor(new NamedThreadFactory("tinyid-generator"));

    /**
     * 异步加载下一个号段
     */
    public void loadNext() {
        if (next == null && !isLoadingNext) {
            synchronized (lock) {
                // 双重检锁
                if (next == null && !isLoadingNext) {
                    isLoadingNext = true;
                    // 异步获取下一个号段
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 无论获取下个segmentId成功与否，都要将isLoadingNext赋值为false
                                next = querySegmentId();
                            } finally {
                                isLoadingNext = false;
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * 获取下一个id
     * 核心流程
     */
    @Override
    public Long nextId() {
        // while-true 确保能拿到id(重试机制)
        // 应该是为了代码更加简洁，也可以在loadCurrent之后，再加个判断
        while (true) {
            if (current == null) {
                loadCurrent();
                continue;
            }
            Result result = current.nextId();
            if (result.getCode() == ResultCode.OVER) {
                // 当前号段用完了
                loadCurrent();
            } else {
                if (result.getCode() == ResultCode.LOADING) {
                    // 达到某个阈值，异步去加载下一个号段的值
                    loadNext();
                }
                return result.getId();
            }
        }
    }

    /**
     * 批量获取下一个id
     * 就是简单循环调用nextId并放到容器中
     * @param batchSize
     * @return
     */
    @Override
    public List<Long> nextId(Integer batchSize) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            Long id = nextId();
            ids.add(id);
        }
        return ids;
    }

}
