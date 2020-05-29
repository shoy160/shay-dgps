package org.shay.dgps.utils.transform;

import io.netty.buffer.ByteBuf;

/**
 * @author shay
 */
public class ByteBufUtils {

    /**
     * BCC校验(异或校验)
     */
    public static byte bcc(ByteBuf byteBuf) {
        byte cs = 0;
        while (byteBuf.isReadable()) {
            cs ^= byteBuf.readByte();
        }
        byteBuf.resetReaderIndex();
        return cs;
    }
}