package com.chulman.microservice.api.apns;

import com.chulman.microservice.notification.domain.model.NotificationResult;
import com.chulman.microservice.notification.domain.repository.NotificationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.Charset;
import java.util.Map;

@Slf4j
public class ApnsResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private final ObjectMapper MAPPER = new ObjectMapper();
    @Autowired
    NotificationRepository notificationRepository;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {

        NotificationResult notificationResult = null;

        String apns_id = msg.headers().entries().stream()
                                                .filter(s -> s.getKey().equalsIgnoreCase("apns-id"))
                                                .map(Map.Entry::getValue)
                                                .findAny().get();
        if (msg.status().code() == 200) {

            notificationResult = NotificationResult.builder().statusCode(NotificationResult.valueOf(msg.status().code()))
                                                             .apns_id(apns_id)
                                                             .build();
        } else {

            ByteBuf byteBufMessage = msg.content();
            int size = byteBufMessage.readableBytes();
            byte[] reads = new byte[size];
            byteBufMessage.readBytes(reads);
            String contents = new String(reads, Charset.defaultCharset());

            Map<String, String> bodys = MAPPER.readValue(contents, new TypeReference<Map<String, String>>(){});

            if (msg.status().code() == 413) {
                notificationResult = NotificationResult.builder().statusCode(NotificationResult.valueOf(msg.status().code()))
                                                                 .apns_id(apns_id)
                                                                 .reason(apns_id)
                                                                 .timeStamp(bodys.get("timestamp"))
                                                                 .build();
            } else {
                notificationResult = NotificationResult.builder().statusCode(NotificationResult.valueOf(msg.status().code()))
                                                                 .apns_id(apns_id)
                                                                 .reason(bodys.get("reason"))
                                                                 .build();
            }
        }

        log.info("Apns Response : {}", notificationResult.toString());

        notificationRepository.update(notificationResult).subscribe();

    }
}
