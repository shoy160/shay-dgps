package org.shay.dgps.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.shay.dgps.annotation.Property;
import org.shay.dgps.enums.DataType;
import org.shay.dgps.mapping.Handler;
import org.shay.dgps.mapping.HandlerMapper;
import org.shay.dgps.message.AbstractMessage;
import org.shay.dgps.message.AbstractMessageBody;
import org.shay.dgps.utils.PropertyUtils;
import org.shay.dgps.utils.bean.BeanUtils;
import org.shay.dgps.utils.transform.Bcd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础消息解码
 */
public abstract class MessageDecoder extends ByteToMessageDecoder {

    private HandlerMapper handlerMapper;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public MessageDecoder(HandlerMapper handlerMapper) {
        this.handlerMapper = handlerMapper;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int type = getType(in);
        Handler handler = handlerMapper.getHandler(type);

        if (handler == null) {
            logger.warn(String.format("未知的处理器：%s", type));
            return;
        }

        Type[] types = handler.getTargetParameterTypes();
        if (types[0] instanceof ParameterizedTypeImpl) {
            ParameterizedTypeImpl clazz = (ParameterizedTypeImpl) types[0];

            Class<? extends AbstractMessageBody> bodyClass = (Class<? extends AbstractMessageBody>) clazz.getActualTypeArguments()[0];
            Class<? extends AbstractMessage> messageClass = (Class<? extends AbstractMessage>) clazz.getRawType();
            AbstractMessage<? extends AbstractMessageBody> decode = decode(in, messageClass, bodyClass);
            out.add(decode);
        } else {
            AbstractMessage<? extends AbstractMessageBody> decode = decode(in, (Class) types[0], null);
            out.add(decode);
        }

        in.skipBytes(in.readableBytes());
    }

    /**
     * 解析
     */
    public <T extends AbstractMessageBody> AbstractMessage<T> decode(ByteBuf buf, Class<? extends AbstractMessage> clazz, Class<T> bodyClass) {
        buf = unEscape(buf);

        if (check(buf)) {
            System.out.println("校验码错误" + ByteBufUtil.hexDump(buf));
        }

        AbstractMessage message = decode(buf, clazz);
        if (bodyClass != null) {
            Integer headerLength = message.getHeaderLength();
            buf.setIndex(headerLength, headerLength + message.getBodyLength());
            T body = decode(buf, bodyClass);
            message.setBody(body);
        }
        return message;
    }

    /**
     * 获取消息类型
     */
    public abstract int getType(ByteBuf buf);

    /**
     * 反转义
     */
    public abstract ByteBuf unEscape(ByteBuf buf);

    /**
     * 校验
     */
    public abstract boolean check(ByteBuf buf);

    public <T> T decode(ByteBuf buf, Class<T> targetClass) {
        T result = BeanUtils.newInstance(targetClass);

        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptor(targetClass);
        for (PropertyDescriptor pd : pds) {

            Method readMethod = pd.getReadMethod();
            Property prop = readMethod.getDeclaredAnnotation(Property.class);
            int length = PropertyUtils.getLength(result, prop);
            if (!buf.isReadable(length)) {
                break;
            }

            if (length == -1) {
                length = buf.readableBytes();
            }
            Object value = null;
            try {
                value = read(buf, prop, length, pd);
            } catch (Exception e) {
                e.printStackTrace();
            }
            BeanUtils.setValue(result, pd.getWriteMethod(), value);
        }
        return result;
    }

    public Object read(ByteBuf buf, Property prop, int length, PropertyDescriptor pd) {
        DataType type = prop.type();

        if (type == DataType.BYTE) {
            return (int) buf.readUnsignedByte();
        } else if (type == DataType.WORD) {
            return buf.readUnsignedShort();
        } else if (type == DataType.DWORD) {
            if (pd.getPropertyType().isAssignableFrom(Long.class)) {
                return buf.readUnsignedInt();
            }
            return (int) buf.readUnsignedInt();
        } else if (type == DataType.STRING) {
            return buf.readCharSequence(length, Charset.forName(prop.charset())).toString().trim();
        } else if (type == DataType.OBJ) {
            return decode(buf.readSlice(length), pd.getPropertyType());
        } else if (type == DataType.LIST) {
            List list = new ArrayList();
            Type clazz = ((ParameterizedType) pd.getReadMethod().getGenericReturnType()).getActualTypeArguments()[0];
            ByteBuf slice = buf.readSlice(length);
            while (slice.isReadable()) {
                list.add(decode(slice, (Class) clazz));
            }
            return list;
        }

        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        if (type == DataType.BCD8421) {
            return Bcd.encode8421String(bytes).trim();
        }
        return bytes;
    }
}