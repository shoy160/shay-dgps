package org.shay.dgps.host.config;

import org.shay.dgps.annotation.Endpoint;
import org.shay.dgps.host.netty.NettyHost;
import org.shay.dgps.mapping.HandlerMapper;
import org.shay.dgps.mapping.SpringHandlerMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author shay
 * @date 2020/5/29
 */
@Configuration
public class NettyConfig {

    @Value("${dgps.port}")
    private int port;

    @Resource
    private ApplicationContext appContext;

    @Bean
    public NettyHost gpsServer() {
        NettyHost server = new NettyHost(port);
        server.startServer();
        return server;
    }
}
