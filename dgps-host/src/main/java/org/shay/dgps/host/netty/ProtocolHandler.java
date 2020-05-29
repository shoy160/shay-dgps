package org.shay.dgps.host.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.shay.dgps.mapping.DefaultHandlerMapper;
import org.shay.dgps.mapping.HandlerMapper;
import org.shay.dgps.protocol.jt808.Jt808Endpoint;
import org.shay.dgps.protocol.jt808.codec.JT808MessageDecoder;
import org.shay.dgps.protocol.jt808.codec.JT808MessageEncoder;
import org.shay.dgps.session.Session;
import org.shay.dgps.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 协议处理
 *
 * @author shay
 * @date 2020/5/29
 */
public class ProtocolHandler extends ByteToMessageDecoder {

    private final SessionManager sessionManager = SessionManager.getInstance();
    private static final Logger log = LoggerFactory.getLogger(ProtocolHandler.class);
    private final byte Jt808Type = 0x7e;


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        byte type = byteBuf.readByte();
        if (Jt808Type == type) {
            byteBuf.resetReaderIndex();
            HandlerMapper handlerMapper = new DefaultHandlerMapper(Jt808Endpoint.class);
            ChannelPipeline pipe = channelHandlerContext.pipeline();
            // 1024表示单条消息的最大长度，解码器在查找分隔符的时候，达到该长度还没找到的话会抛异常
            pipe.addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.wrappedBuffer(new byte[]{type}), Unpooled.wrappedBuffer(new byte[]{type, type})));
            pipe.addLast(new JT808MessageDecoder(handlerMapper));
            pipe.addLast(new JT808MessageEncoder());
            pipe.addLast(new GpsHandler(handlerMapper));
            pipe.remove(this);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Session session = Session.buildSession(ctx.channel());
        sessionManager.put(session.getId(), session);
        log.info(String.format("终端连接,%s", session.getRemoteAddr()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String sessionId = Session.buildId(ctx.channel());
        Session session = sessionManager.removeBySessionId(sessionId);
        log.info(String.format("断开连接,%s", session.getRemoteAddr()));
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        String sessionId = Session.buildId(ctx.channel());
        Session session = sessionManager.getBySessionId(sessionId);
        log.error(String.format("发生异常,%s", session.getRemoteAddr()));
        e.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                Session session = this.sessionManager.removeBySessionId(Session.buildId(ctx.channel()));
                log.debug(String.format("服务器主动断开连接,%s", session.getRemoteAddr()));
                ctx.close();
            }
        }
    }
}
