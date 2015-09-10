package netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by MyPC on 2015/8/16.
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcDecoder.class);

    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4)
            return;

        LOGGER.warn(genericClass.getName() + " decoder");
        // mark before read
        byteBuf.markReaderIndex();
        int datalen = byteBuf.readInt();
        if (datalen < 0)
            channelHandlerContext.close();
        if (byteBuf.readableBytes() < datalen)
            byteBuf.resetReaderIndex(); // reset index
        byte[] data = new byte[datalen];
        byteBuf.readBytes(data);

        Object obj = SerializationUtil.deserialize(data, genericClass);
        list.add(obj);
    }
}
