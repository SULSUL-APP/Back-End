package com.example.sulsul.exception.notFound;

import com.example.sulsul.exception.BaseErrorCode;
import com.example.sulsul.exception.BaseException;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
