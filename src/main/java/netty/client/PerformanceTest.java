package netty.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by MyPC on 2015/9/9.
 * <p/>
 * i3-2350M (4 cores)
 * ==BIO score==
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-client.xml")
public class PerformanceTest {
    private static AtomicLong amount = new AtomicLong(0L);
    private static CountDownLatch cdl;

    @Autowired
    private RpcProxy rpcProxy;

    @Test
    public void helloTest() {

        int coreCount = Runtime.getRuntime().availableProcessors() * 2;
        cdl = new CountDownLatch(coreCount);

        long startTime = System.currentTimeMillis();
        final ExecutorService es = Executors.newFixedThreadPool(coreCount);
        for (int i = 0; i < coreCount; ++i) {
            es.execute(new Runnable() {
                @Override
                public void run() {
                    while (amount.get() < 10_000) {
                        try {
                            // Netty
                            netty.server.services.HelloService helloService = rpcProxy.create(netty.server.services.HelloService.class);
                            helloService.hello("jarvis");
                            amount.incrementAndGet();
                        } catch (Exception e) {
                            continue;
                        }
                    }
                    cdl.countDown();
                }
            });
        }
        try {
            cdl.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            es.shutdown();
        }
        long endTime = System.currentTimeMillis();

        double tps = 1.0 * amount.get() / (endTime - startTime) * 1000;
        System.out.println("TPS: " + tps);
    }

}
