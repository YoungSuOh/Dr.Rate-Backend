package com.bitcamp.drrate.global.exception.exceptionhandler;

import com.bitcamp.drrate.global.code.ErrorCode;
import com.bitcamp.drrate.global.exception.GeneralException;

public class StompServiceExceptionHandler extends GeneralException {
    public StompServiceExceptionHandler(ErrorCode errorCode) {
        super(errorCode);
    }
}
