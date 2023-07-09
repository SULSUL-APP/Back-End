package com.example.sulsul.exception.notFound;

public class UTypeNotFoundException extends ResourceNotFoundException {
    public UTypeNotFoundException() {
        super(ResourceNotFoundErrorCode.UTYPE_NOT_FOUND);
    }
}
