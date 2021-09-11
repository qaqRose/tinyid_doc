package com.xiaoju.uemc.tinyid.server;

import com.xiaoju.uemc.tinyid.base.generator.IdGenerator;
import com.xiaoju.uemc.tinyid.server.factory.impl.IdGeneratorFactoryServer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author du_imba
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerTest {

    @Autowired
    IdGeneratorFactoryServer idGeneratorFactoryServer;

    @Test
    public void testNextId() {
        IdGenerator idGenerator = idGeneratorFactoryServer.getIdGenerator("test");
        Long id = idGenerator.nextId();
        System.out.println("current id is: " + id);
    }

    @Test
    public void testConcurrentNextId() throws InterruptedException {

        // 并发线程
        int n = 10;
        // 每个获取的id数量
        int m = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(n);
        final Map<Long, Object> map = new ConcurrentHashMap(n*m*2);
        final Object value = new Object();

        IdGenerator idGenerator = idGeneratorFactoryServer.getIdGenerator("test");

        CountDownLatch latch = new CountDownLatch(n);
        // 并发测试
        for (int i = 0; i < n; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < m; j++) {
                    Long id = idGenerator.nextId();
                    map.put(id, value);
                }
                latch.countDown();
            });
        }
        latch.await();
        Assert.assertEquals(map.keySet().size(), n * m);
    }
}
