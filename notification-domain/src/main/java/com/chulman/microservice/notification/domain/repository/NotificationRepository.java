package com.chulman.microservice.notification.domain.repository;

import com.chulman.microservice.notification.domain.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.sql.Types;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_QUERY = "INSERT INTO NOTIFICATION(DEVICE_TOKEN, OS, BUNDLE, PAYLOAD) VALUES(?,?,?,?)";


    public Observable<Integer> insert(Notification notification) {
        Object[] param = {notification.getDeviceToken(), notification.getOs(), notification.getBundle(), notification.getPayload()};
        int[] type = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR ,Types.VARCHAR};

        return Observable.fromCallable(() -> jdbcTemplate.update(INSERT_QUERY, param, type))
                         .doOnNext(integer -> log.info("insert notification data={}, result={}",notification, integer))
                         .doOnError(throwable -> throwable.printStackTrace())
                         .subscribeOn(Schedulers.io()); //subscribeOn은 Observable 객체가 실행될 쓰레드를 정한다.
    }
}
