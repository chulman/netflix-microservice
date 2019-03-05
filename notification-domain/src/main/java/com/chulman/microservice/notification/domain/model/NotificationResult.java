package com.chulman.microservice.notification.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResult {

    private String apns_id;
    private StatusCode statusCode;

    private String reason;
    private String timeStamp;


    public static StatusCode valueOf(int headerCode) {
        switch (headerCode) {
            case 200:
                return StatusCode.Success;
            case 400:
                return StatusCode.BadReqest;
            case 403:
                return StatusCode.UnAuthorized;
            case 405:
                return StatusCode.InvalidMethod;
            case 410:
                return StatusCode.InActiveDeviceToken;
            case 413:
                return StatusCode.PayLoadTooLarge;
            case 429:
                return StatusCode.TooManyRequestSameDevice;
            case 500:
                return StatusCode.InternalServerError;
            case 503:
                return StatusCode.Unavailable;
        }
        return null;
    }

    enum StatusCode {

        Success(200),

        BadReqest(400),

        UnAuthorized(403),

        InvalidMethod(405),

        InActiveDeviceToken(410),

        PayLoadTooLarge(413),

        TooManyRequestSameDevice(429),

        InternalServerError(500),

        Unavailable(503);

        private int value;

        private StatusCode(int value) {
            this.value = value;
        }

    }
}
