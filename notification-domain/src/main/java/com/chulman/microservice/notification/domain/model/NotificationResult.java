package com.chulman.microservice.notification.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResult {

    private String apns_id;
    private ApnsHttpsCode apnsHttpsCode;

    private String reason;
    private String timeStamp;


    public static ApnsHttpsCode valueOf(int headerCode) {
        switch (headerCode) {
            case 200:
                return ApnsHttpsCode.Success;
            case 400:
                return ApnsHttpsCode.BadReqest;
            case 403:
                return ApnsHttpsCode.UnAuthorized;
            case 405:
                return ApnsHttpsCode.InvalidMethod;
            case 410:
                return ApnsHttpsCode.InActiveDeviceToken;
            case 413:
                return ApnsHttpsCode.PayLoadTooLarge;
            case 429:
                return ApnsHttpsCode.TooManyRequestSameDevice;
            case 500:
                return ApnsHttpsCode.InternalServerError;
            case 503:
                return ApnsHttpsCode.Unavailable;
        }
        return null;
    }

    enum ApnsHttpsCode {

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

        private ApnsHttpsCode(int value) {
            this.value = value;
        }

    }
}
