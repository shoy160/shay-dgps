package org.shay.dgps.codec;


import org.shay.dgps.message.AbstractMessageBody;

/**
 * 基础编码接口
 *
 * @author shay
 */
public interface BaseCodec {
    /**
     * 解析tcp报文信息
     *
     * @param rowData 原始tcp报文
     * @return 消息
     */
    AbstractMessageBody decode(byte[] rowData);

    /**
     * 将消息封装成tcp报文
     *
     * @param message 消息
     * @return tcp报文
     */
    byte[] encode(AbstractMessageBody message);
}
