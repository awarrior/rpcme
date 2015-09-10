package bio;

import java.util.concurrent.TimeUnit;

/**
 * Created by MyPC on 2015/7/24.
 */
public class RpcConsumer {
    public static void main(String[] args) {
        try {
            HelloService service = RpcFramework.refer(HelloService.class, "127.0.0.1", 10086);
            for (int i = 0; i < Integer.MAX_VALUE; ++i) {
                service.hello("jarvis");
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
