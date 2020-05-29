package org.shay.dgps.host.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.shay.dgps.mapping.Handler;
import org.shay.dgps.mapping.HandlerMapper;
import org.shay.dgps.message.AbstractMessage;
import org.shay.dgps.session.Session;
import org.shay.dgps.session.SessionManager;
import org.shay.dgps.utils.transform.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * @author shay
 */
public class GpsHandler extends ChannelInboundHandlerAdapter {

    private final SessionManager sessionManager = SessionManager.getInstance();
    private static final Logger log = LoggerFactory.getLogger(NettyHost.class);

    private HandlerMapper handlerMapper;

    public GpsHandler(HandlerMapper handlerMapper) {
        this.handlerMapper = handlerMapper;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            AbstractMessage messageRequest = (AbstractMessage) msg;

            log.info(JsonUtils.toJson(messageRequest));
            Channel channel = ctx.channel();

            Handler handler = handlerMapper.getHandler(messageRequest.getType());

            Type[] types = handler.getTargetParameterTypes();
            Session session = sessionManager.getBySessionId(Session.buildId(channel));

            AbstractMessage messageResponse;
            if (types.length == 1) {
                messageResponse = handler.invoke(messageRequest);
            } else {
                messageResponse = handler.invoke(messageRequest, session);
            }

            //应答
            if (messageResponse != null) {
                ChannelFuture future = channel.writeAndFlush(messageResponse).sync();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
