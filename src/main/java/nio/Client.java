package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {
    private Selector selector;

    public void init(String ip, int port) {
        try {
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(ip, port));
            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_CONNECT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        boolean finish = false;
        while (true) {
            try {
                selector.select();
                try {
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();

                        if (key.isConnectable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            if (channel.isConnectionPending())
                                channel.finishConnect();
                            channel.configureBlocking(false);
                            channel.register(selector, SelectionKey.OP_READ);

                            // send message
                            ByteBuffer outBuffer = ByteBuffer.wrap(("message from client").getBytes());
                            channel.write(outBuffer);

                        } else if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(50);
                            channel.read(buffer);
                            byte[] data = buffer.array();
                            String msg = new String(data).trim();
                            System.out.println("CLIENT: " + msg);

                            channel.finishConnect();
                            channel.close();
                            finish = true;
                        }
                    }
                    if (finish) {
                        selector.close();
                        break;
                    }
                } catch (IOException e) {
                    selector.close();
                    throw e;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.init("localhost", 8000);
        client.listen();
    }

}
