package com.bitcamp.drrate.global.exception.exceptionhandler;

import com.bitcamp.drrate.global.code.ErrorCode;
import com.bitcamp.drrate.global.exception.GeneralException;

public class CalendarServiceExceptionHandler extends GeneralException {
    public CalendarServiceExceptionHandler(ErrorCode errorCode) {
        super(errorCode);
    }
}
