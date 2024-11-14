package com.bitcamp.drrate.global.exception;


import com.bitcamp.drrate.global.code.ErrorCode;
import com.bitcamp.drrate.global.code.ErrorDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private ErrorCode code;

    public ErrorDTO getErrorReason(){
        return this.code.getReason();
    }
    public ErrorDTO getErrorReasonHttpStatus(){
        return this.code.getHttpStatusReason();
    }
}
