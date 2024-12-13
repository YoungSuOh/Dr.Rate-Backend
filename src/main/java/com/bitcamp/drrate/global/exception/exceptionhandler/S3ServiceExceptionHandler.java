package com.bitcamp.drrate.global.exception.exceptionhandler;

import com.bitcamp.drrate.global.code.ErrorCode;
import com.bitcamp.drrate.global.exception.GeneralException;

public class S3ServiceExceptionHandler extends GeneralException {
    public S3ServiceExceptionHandler(ErrorCode errorCode) {
        super(errorCode);
    }
}