package com.chulman.microservice.api.apns;

import com.chulman.microservice.api.exception.AuthenticationException;
import com.chulman.microservice.notification.domain.model.Notification;
import com.chulman.microservice.notification.domain.model.NotificationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;

import java.io.IOException;


@Slf4j
public class ApnsConnector {

    private final ObjectMapper MAPPER = new ObjectMapper();

    private ApnsResponseHandler apnsResponseHandler;
    private ApnsInitializer apnsInitializer;


    private Channel channel;
    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;


    private String host = "api.development.push.apple.com";
    private int port = 443;

    private String token;


    public ApnsConnector(ApnsResponseHandler apnsResponseHandler) {
        this.apnsResponseHandler = apnsResponseHandler;
    }

    public boolean connect() throws Exception {
        eventLoopGroup = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new ApnsInitializer(apnsResponseHandler));

        ChannelFuture cf = bootstrap.connect(host, port).sync();
        channel = cf.channel();

        log.info("connected : {}, apns-end-point: {}", cf.isSuccess(), cf.channel().remoteAddress());
        return cf.isSuccess();
    }

    public ChannelFuture send(Notification notification, HttpHeaders httpHeaders) throws AuthenticationException {

        log.info("deviceToken[{}], payload {}", notification.getDeviceToken(), notification.getPayload());
        httpHeaders.entries().stream().forEach(header -> log.info("headers {}", header));
        FullHttpRequest request;
        String message = "";
        try {
            message = MAPPER.writeValueAsString(notification.getPayload());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                "https://" + host + "/3/device/" + notification.getDeviceToken(), Unpooled.copiedBuffer(message.getBytes()),
                new DefaultHttpHeaders(), httpHeaders);

        ChannelFuture channelFuture = channel.writeAndFlush(request);
        return channelFuture;
    }


    public void close() throws IOException {
        channel.close();
        eventLoopGroup.shutdownGracefully();
        log.info("close : {}, apns-end-point: {}", channel.toString());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setHost(String host) {
        this.host = host;
    }
}