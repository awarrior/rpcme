package netty.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by MyPC on 2015/8/17.
 *
 * Before you start the main function
 * You should run zookeeper/bin/zkServer first
 */
public class RpcBoostrap {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-server.xml");
    }

}
