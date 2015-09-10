package bio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by MyPC on 2015/7/24.
 */
public class RpcFramework {

    // server
    // export the port of service
    public static void export(final Object service, int port) throws Exception {
        if (service == null)
            throw new IllegalArgumentException("service is null");
        if (port <= 0 || port > 65535)
            throw new IllegalArgumentException("invalid port");

        System.out.println("export service " + service.getClass().getName() + " on port " + port);

        try {
            // BIO server
            ServerSocket server = new ServerSocket(port);
            while (true) {
                // accept a client request
                final Socket socket = server.accept();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            try {
                                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                                try {
                                    // read method: name, param-type, args
                                    String methodName = input.readUTF();
                                    Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                                    Object[] arguments = (Object[]) input.readObject();

                                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                                    try {
                                        // reflect
                                        Method method = service.getClass().getMethod(methodName, parameterTypes);
                                        Object result = method.invoke(service, arguments);
                                        // write result object
                                        output.writeObject(result);
                                    } catch (Exception e) {
                                        output.writeObject(e);
                                    } finally {
                                        output.close();
                                    }
                                } catch (ClassNotFoundException e) {
                                    System.out.println("object not found");
                                } finally {
                                    input.close();
                                }
                            } finally {
                                socket.close();
                            }
                        } catch (IOException e) {
                            System.out.println("object input stream");
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println("server socket io error");
            e.printStackTrace();
        }
    }

    // client
    // refer the service from (host,port)
    public static <T> T refer(final Class<T> interfaceClass, final String host, final int port) throws Exception {
        if (interfaceClass == null)
            throw new IllegalArgumentException("Interface class==null");
        if (!interfaceClass.isInterface())
            throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class");
        if (host == null || host.isEmpty())
            throw new IllegalArgumentException("Host is null");
        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("invalid port " + port);

        System.out.println("get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);

        // dynamic proxy
        Object obj = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
                // BIO client
                Socket socket = new Socket(host, port);
                try {
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    try {
                        // send method: name, param-type, args
                        output.writeUTF(method.getName());
                        output.writeObject(method.getParameterTypes());
                        output.writeObject(arguments);

//                        output.writeObject(new test());

                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        try {
                            // receive object
                            Object result = input.readObject();
                            if (result instanceof Throwable) {
                                throw (Throwable) result;
                            }
                            return result;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            input.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        output.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    socket.close();
                }
                return new Object();
            }
        });
        return (T) obj;
    }
}
