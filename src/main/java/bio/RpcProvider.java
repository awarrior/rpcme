package bio;

/**
 * Created by MyPC on 2015/7/24.
 */
public class RpcProvider {
    public static void main(String[] args) {
        HelloService service = new HelloServiceImpl();
        try {
            RpcFramework.export(service, 10086);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
