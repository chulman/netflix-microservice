package com.chulman.microservice.api.apns;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http2.*;
import io.netty.handler.logging.LogLevel;
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
    private Http2Connection http2Connection;

    public ApnsInitializer(ApnsResponseHandler apnsResponseHandler, Http2Connection http2Connection){
        this.apnsResponseHandler = apnsResponseHandler;
        this.http2Connection = http2Connection;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {


        httpToHttp2ConnectionHandlerBuilder = new HttpToHttp2ConnectionHandlerBuilder()
                .frameListener(new DelegatingDecompressorFrameListener(
                        http2Connection,
                        new InboundHttp2ToHttpAdapterBuilder(http2Connection)
                                .maxContentLength(Integer.MAX_VALUE)
                                .propagateSettings(true)
                                .build()))
                .connection(http2Connection)
      //        .initialSettings()    //http2 initail frame setting
                .encoderEnforceMaxConcurrentStreams(true)   // Concurrent stream max is exceed, input encoder queue.
                .gracefulShutdownTimeoutMillis(3000)
                .frameLogger(new Http2FrameLogger(LogLevel.INFO, Http2ConnectionHandler.class))
                .build();

        final SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        SslHandler sslHandler = sslContext.newHandler(ch.alloc());

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(sslHandler);
        pipeline.addLast(httpToHttp2ConnectionHandlerBuilder);
        pipeline.addLast(apnsResponseHandler);
    }
}
