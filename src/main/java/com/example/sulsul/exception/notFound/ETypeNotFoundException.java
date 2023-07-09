package com.example.sulsul.exception.notFound;

public class ETypeNotFoundException extends ResourceNotFoundException {
    public ETypeNotFoundException() {
        super(ResourceNotFoundErrorCode.ETYPE_NOT_FOUND);
    }
}
