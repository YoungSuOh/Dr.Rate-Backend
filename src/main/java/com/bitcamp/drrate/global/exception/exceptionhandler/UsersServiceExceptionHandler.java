package com.bitcamp.drrate.global.exception.exceptionhandler;

import com.bitcamp.drrate.global.code.ErrorCode;
import com.bitcamp.drrate.global.exception.GeneralException;

public class UsersServiceExceptionHandler extends GeneralException {
  public UsersServiceExceptionHandler(ErrorCode errorCode) {
    super(errorCode);
  }
}
