package com.chulman.microservice.api.service;

import com.chulman.microservice.api.apns.ApnsConnector;
import com.chulman.microservice.api.utils.ApnsHeader;
import com.chulman.microservice.notification.domain.model.Notification;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.functions.Action0;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApnsProvider {

    @Autowired
    private ApnsConnector apnsConnector;

//    public Observable<Void> send(Notification notification) {
//
//        notification.setApns_id(UUID.randomUUID().toString());
//        ChannelFuture future = apnsConnector.send(notification, getHeader(notification));
//        return Observable.from(future).filter(aVoid -> future.isSuccess())
//                                      .doOnNext(aVoid -> log.info("send future : {}", future.isSuccess()))
//                                      .doOnError(throwable -> log.error("{}", throwable.getCause()));
//    }

    /**
     * set Circuit-breaker pattern
     *
     * @param notification
     * @return Observable
     */
    public Observable<Object> send(Notification notification) {

        notification.setApns_id(UUID.randomUUID().toString());
        return new ApnsCommand(apnsConnector, notification, getHeader(notification)).toObservable();

    }

    public void close(){
        try {
            apnsConnector.close();
        } catch (IOException e) {
            new RuntimeException("to apns server close exception");
        }
    }

    private HttpHeaders getHeader(Notification notification) {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(ApnsHeader.AUTHORIZATION, "Bearer " + apnsConnector.getToken());
        headers.add(ApnsHeader.ID, notification.getApns_id());
        headers.add(ApnsHeader.TOPIC, notification.getBundle());
        headers.add(ApnsHeader.PRIORITY, 10);
//        headers.add(ApnsHeader.EXPIRATION, expire);
//        headers.add(ApnsHeader.COLLAPSE, collapse);

        return headers;
    }



    static class ApnsCommand extends HystrixObservableCommand<Object> {

        private static Setter setter = Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(ApnsCommand.class.getSimpleName()))
                .andCommandKey(HystrixCommandKey.Factory.asKey(ApnsCommand.class.getSimpleName()))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)  // semaphore strategy
                                .withExecutionIsolationSemaphoreMaxConcurrentRequests(3) // failure 3 requsts, circuit on.
                                .withExecutionTimeoutEnabled(true)
                                .withExecutionTimeoutInMilliseconds(100)
                );

        private final ApnsConnector apnsConnector;
        private Notification notification;
        private HttpHeaders httpHeaders;


        public ApnsCommand(ApnsConnector apnsConnector, Notification notification, HttpHeaders httpHeaders) {
            super(setter);
            this.apnsConnector = apnsConnector;
            this.notification = notification;
            this.httpHeaders = httpHeaders;
        }

        @Override
        protected Observable<Object> construct() {

            Observable observable = null;
            try {
                ChannelFuture future = apnsConnector.send(notification,httpHeaders);
                observable = Observable.from(future)
                                        .doOnNext(aVoid -> log.info("channel {}, send = {}", (future.isSuccess()) ? "hit" : "miss", future.isSuccess()))
                                        .doOnError(throwable ->{
                                            log.error("circuit open? = {}, {}",isCircuitBreakerOpen(),throwable.getCause());
                                        })
                                        .switchMap(aVoid -> Observable.just(future.isSuccess()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }finally {
                return observable;
            }
        }

        // fallback is false.
        @Override
        protected Observable<Object> resumeWithFallback() {
            return Observable.just(false);
        }
    }

}
