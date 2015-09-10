package netty.server.services;

import netty.server.RpcService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyPC on 2015/8/17.
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    private List<String> info;

    public HelloServiceImpl() {
        info = new ArrayList<>();
        info.add("test");
    }

    @Override
    public String hello(String name) {
        return "hello: " + name;
    }
}
