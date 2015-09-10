package bio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyPC on 2015/7/24.
 */
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
