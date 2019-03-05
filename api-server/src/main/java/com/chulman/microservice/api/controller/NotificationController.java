package com.chulman.microservice.api.controller;

import com.chulman.microservice.api.service.NotificationService;
import com.chulman.microservice.notification.domain.model.Notification;
import com.chulman.microservice.notification.domain.model.NotificationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

import javax.validation.Valid;

// cold observable
@Controller
@RestController
@RequestMapping("/api/notification/v1")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @RequestMapping(value = "/apns/send", method = RequestMethod.POST)
    public DeferredResult<Integer> sendToApns(@RequestBody @Valid Notification notification){
        return toDeferredResult(notificationService.sendToApns(notification));
    }


    private static <T> DeferredResult<T> toDeferredResult(Observable<T> observable) {
        DeferredResult<T> deferredResult = new DeferredResult<>();
        observable.subscribe(deferredResult::setResult, deferredResult::setErrorResult);
        return deferredResult;
    }
}
