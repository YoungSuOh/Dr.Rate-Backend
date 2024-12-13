package com.bitcamp.drrate.global.exception.exceptionhandler;

import com.bitcamp.drrate.global.code.ErrorCode;
import com.bitcamp.drrate.global.exception.GeneralException;

public class UserServiceExceptionHandler extends GeneralException {
    public UserServiceExceptionHandler(ErrorCode errorCode) {
        super(errorCode);
    }
}
