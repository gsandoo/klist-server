package com.kk.klist.domain.bucketlist.domain.exception;

import com.kk.klist.global.exception.BusinessException;

public class BucketListException extends BusinessException {

    public BucketListException(BucketListErrorCode errorCode) {
        super(errorCode);
    }
}
