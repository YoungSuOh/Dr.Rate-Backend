package com.bitcamp.drrate.global.exception.exceptionhandler;

import com.bitcamp.drrate.global.code.ErrorCode;
import com.bitcamp.drrate.global.exception.GeneralException;


public class InquireServiceHandler extends GeneralException {
    public InquireServiceHandler(ErrorCode errorCode) {
        super(errorCode);
    }
}