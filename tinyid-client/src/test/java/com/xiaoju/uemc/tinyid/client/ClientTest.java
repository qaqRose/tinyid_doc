package com.xiaoju.uemc.tinyid.client;

import com.xiaoju.uemc.tinyid.client.utils.TinyId;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author du_imba
 */

public class ClientTest {

    @Test
    public void testNextId() {
        for (int i = 0; i < 100; i++) {
            Long id = TinyId.nextId("test");
            System.out.println("current id is: " + id);
        }
    }

    /**
     * 并发测试
     * @throws InterruptedException
     */
    @Test
    public void testConcurrentNextId() throws InterruptedException {

        // 并发线程
        int n = 10;
        // 每个获取的id数量
        int m = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(n);
        final Map<Long, Object> map = new ConcurrentHashMap(n*m*2);
        final Object value = new Object();

        CountDownLatch latch = new CountDownLatch(n);
        // 并发测试
        for (int i = 0; i < n; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < m; j++) {
                    Long id = TinyId.nextId("test");
                    map.put(id, value);
                }
                latch.countDown();
            });
        }
        latch.await();
        Assert.assertEquals(map.keySet().size(), n * m);
    }

    @Test
    public void testBatchNextId() {
        List<Long> list = TinyId.nextId("test", 1000);
        for (Long id : list) {
            System.out.println("id:" + id);
        }
    }


}
