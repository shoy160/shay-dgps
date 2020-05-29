package org.shay.dgps.protocol.jt808.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.shay.dgps.codec.MessageEncoder;
import org.shay.dgps.utils.transform.ByteBufUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 808协议编码器
 *
 * @author shay
 */
public class JT808MessageEncoder extends MessageEncoder {

    @Override
    public ByteBuf sign(ByteBuf buf) {
        byte checkCode = ByteBufUtils.bcc(buf);
        buf.writeByte(checkCode);
        return buf;
    }

    /**
     * 转义处理
     */
    @Override
    public ByteBuf escape(ByteBuf source) {
        int low = source.readerIndex();
        int high = source.writerIndex();

        int mark = source.forEachByte(low, high, value -> !(value == 0x7d || value == 0x7e));

        if (mark == -1) {
            return source;
        }

        List<ByteBuf> bufList = new ArrayList<>(5);

        int len;
        do {

            len = mark + 1 - low;
            ByteBuf[] slice = slice(source, low, len);
            bufList.add(slice[0]);
            bufList.add(slice[1]);
            low += len;

            mark = source.forEachByte(low, high - low, value -> !(value == 0x7d || value == 0x7e));
        } while (mark > 0);

        bufList.add(source.slice(low, high - low));

        ByteBuf[] bufs = bufList.toArray(new ByteBuf[bufList.size()]);

        return Unpooled.wrappedBuffer(bufs);
    }

    /**
     * 截断转义前报文，并转义
     */
    private ByteBuf[] slice(ByteBuf byteBuf, int index, int length) {
        byte first = byteBuf.getByte(index + length - 1);

        ByteBuf[] bufs = new ByteBuf[2];
        bufs[0] = byteBuf.slice(index, length);

        // 0x01 不做处理 p47
        if (first == 0x7d) {
            bufs[1] = Unpooled.wrappedBuffer(new byte[]{0x01});
        } else {
            byteBuf.setByte(index + length - 1, 0x7d);
            bufs[1] = Unpooled.wrappedBuffer(new byte[]{0x02});
        }

        return bufs;
    }
}