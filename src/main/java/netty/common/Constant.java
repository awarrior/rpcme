package netty.common;

/**
 * Created by MyPC on 2015/8/17.
 */
public interface Constant {
    int ZK_SESSION_TIMEOUT  = 5000;
    String ZK_REGISTRY_PATH = "/registry";
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
}
