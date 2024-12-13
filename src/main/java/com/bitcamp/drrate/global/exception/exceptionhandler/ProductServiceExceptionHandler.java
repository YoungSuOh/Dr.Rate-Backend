package com.bitcamp.drrate.global.exception.exceptionhandler;

import com.bitcamp.drrate.global.code.ErrorCode;
import com.bitcamp.drrate.global.exception.GeneralException;

public class ProductServiceExceptionHandler extends GeneralException {
    public ProductServiceExceptionHandler(ErrorCode errorCode) {
        super(errorCode);
    }
}

