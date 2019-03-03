package com.chulman.microservice.api.apns;

import com.chulman.microservice.notification.domain.model.Notification;
import com.chulman.microservice.notification.domain.model.NotificationResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import rx.Observable;
import rx.schedulers.Schedulers;

@Slf4j
public class ApnsResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        log.info("Apns Response Status : {}", msg.status().code());


        ByteBuf byteBufMessage = msg.content();
        int size = byteBufMessage.readableBytes();
        byte[] reads = new byte[size];
        byteBufMessage.readBytes(reads);
        String contents = new String(reads);
        System.err.println(contents);
        JsonNode node = MAPPER.readTree(contents);
        NotificationResult notificationResult = null;

        if (msg.status().code() == 200) {
            notificationResult = NotificationResult.builder().apnsHttpsCode(NotificationResult.valueOf(msg.status().code()))
                                                             .build();
        } else if (msg.status().code() == 413) {
            notificationResult = NotificationResult.builder().apnsHttpsCode(NotificationResult.valueOf(msg.status().code()))
                                                             .reason(node.get("reason").textValue())
                                                             .timeStamp(node.get("timestamp").textValue())
                                                             .build();
        } else {
            notificationResult = NotificationResult.builder().apnsHttpsCode(NotificationResult.valueOf(msg.status().code()))
                                                             .reason(node.get("reason").textValue())
                                                             .build();
        }

    }
}
