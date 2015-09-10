package bio;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by MyPC on 2015/9/9.
 *
 * i3-2350M (4 cores)
 * ==BIO score==
 * TPS: 1417.4220963172806
 * TPS: 1463.6536492613718
 * TPS: 1412.6199887069454
 */
public class PerformanceTest {
    private static AtomicLong amount = new AtomicLong(0L);
    private static CountDownLatch cdl;

    public static void main(String[] args) {
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
                            // BIO
                            HelloService service = RpcFramework.refer(HelloService.class, "127.0.0.1", 10086);
                            service.hello("jarvis");
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
