package com.kk.klist.domain.member.domain.exception;

import com.kk.klist.global.exception.BusinessException;

public class MemberException extends BusinessException {

    public MemberException(MemberErrorCode errorCode) {
        super(errorCode);
    }
}
