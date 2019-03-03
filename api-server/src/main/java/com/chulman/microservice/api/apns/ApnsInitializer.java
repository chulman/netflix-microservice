package com.chulman.microservice.api.apns;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http2.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@ChannelHandler.Sharable
public class ApnsInitializer extends ChannelInitializer<SocketChannel> {


    @Autowired
    private ApnsResponseHandler apnsResponseHandler;
    private HttpToHttp2ConnectionHandler httpToHttp2ConnectionHandlerBuilder;


    public ApnsInitializer(ApnsResponseHandler apnsResponseHandler){
        this.apnsResponseHandler = apnsResponseHandler;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        final Http2Connection http2Connection = new DefaultHttp2Connection(false);
        httpToHttp2ConnectionHandlerBuilder = new HttpToHttp2ConnectionHandlerBuilder()
                .frameListener(new DelegatingDecompressorFrameListener(
                        http2Connection,
                        new InboundHttp2ToHttpAdapterBuilder(http2Connection)
                                .maxContentLength(Integer.MAX_VALUE)
                                .propagateSettings(true)
                                .build()))
                .connection(http2Connection)
                .build();

        final SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        SslHandler sslHandler = sslContext.newHandler(ch.alloc());

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(sslHandler);
        pipeline.addLast(httpToHttp2ConnectionHandlerBuilder);
        pipeline.addLast(apnsResponseHandler);
    }
}
