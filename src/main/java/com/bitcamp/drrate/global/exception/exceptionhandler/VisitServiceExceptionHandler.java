package com.bitcamp.drrate.global.exception.exceptionhandler;

import com.bitcamp.drrate.global.code.ErrorCode;
import com.bitcamp.drrate.global.exception.GeneralException;

public class VisitServiceExceptionHandler extends GeneralException {
    public VisitServiceExceptionHandler(ErrorCode code) {
        super(code);
    }
}
