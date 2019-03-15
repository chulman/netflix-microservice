package com.chulman.microservice.notification.domain.repository;

import com.chulman.microservice.notification.domain.model.Notification;
import com.chulman.microservice.notification.domain.model.NotificationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.sql.Types;
import java.util.concurrent.Executors;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationRepository {

    private final JdbcTemplate jdbcTemplate;

    private String INSERT_NOTIFICATION = "INSERT INTO NOTIFICATION(DEVICE_TOKEN, OS, BUNDLE, PAYLOAD, APNS_ID) VALUES(?,?,?,?,?)";
    private String UPDATE_NOTIFICATION = "UPDATE NOTIFICATION SET STATUS=? WHERE APNS_ID=?";

    private static final Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(10));

    public Observable<Integer> insert(Notification notification) {
        Object[] param = {notification.getDeviceToken(), notification.getOs(), notification.getBundle(), notification.getPayload(), notification.getApns_id()};
        int[] type = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR ,Types.VARCHAR, Types.VARCHAR};

         return Observable.fromCallable(() -> jdbcTemplate.update(INSERT_NOTIFICATION, param, type))
                          .doOnNext(integer -> log.info("insert notification data={}, result={}", notification, integer))
                          .doOnError(throwable ->  log.error("{}", throwable.getCause()))
                          .subscribeOn(scheduler);
    }

    public Observable<Integer> update(NotificationResult notificationResult) {
        Object[] param = {notificationResult.getStatusCode(), notificationResult.getApns_id()};
        int[] type = {Types.VARCHAR, Types.VARCHAR};

        return Observable.fromCallable(() -> jdbcTemplate.update(UPDATE_NOTIFICATION, param, type))
                         .doOnNext(integer -> log.info("update notification data={}, result={}", notificationResult, integer))
                         .doOnError(throwable ->  log.error("{}", throwable.getCause()))
                         .subscribeOn(scheduler);
    }
}
