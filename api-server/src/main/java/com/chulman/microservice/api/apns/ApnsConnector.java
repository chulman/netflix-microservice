package com.chulman.microservice.api.apns;

import com.chulman.microservice.api.exception.AuthenticationException;
import com.chulman.microservice.notification.domain.model.Notification;
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
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.Http2Connection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


@Slf4j
public class ApnsConnector {

    private final ObjectMapper MAPPER = new ObjectMapper();

    private ApnsResponseHandler apnsResponseHandler;

    private Channel channel;
    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;
    private int eventloopThreadCount = 1;
    private Http2Connection http2Connection;

    private String host = "api.development.push.apple.com";
    private int port = 443;

    private String token;

    public ApnsConnector(ApnsResponseHandler apnsResponseHandler) {
        this.apnsResponseHandler = apnsResponseHandler;
    }

    public boolean connect() throws Exception {
        http2Connection = new DefaultHttp2Connection(false);
        eventLoopGroup = new NioEventLoopGroup(eventloopThreadCount);
        bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new ApnsInitializer(apnsResponseHandler,http2Connection));
        ChannelFuture cf = bootstrap.connect(host, port).sync();
        channel = cf.channel();

        log.info("connected : {}, apns-end-point: {}", cf.isSuccess(), cf.channel().remoteAddress());
        log.info("http2 maximum avaliable remote stream count : {}", http2Connection.remote().maxActiveStreams());

        return cf.isSuccess();
    }

    public ChannelFuture send(Notification notification, HttpHeaders httpHeaders) throws Exception {

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

        log.info("send to {}, message={}", notification.getDeviceToken(), notification.getPayload());
        return  channel.writeAndFlush(request);
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

    public void setEventloopThreadCount(int eventloopThreadCount) {
        this.eventloopThreadCount = eventloopThreadCount;
    }

    public Http2Connection getHttp2Connection() {
        return http2Connection;
    }
}