package com.springboot.marketplace.exception;

import org.springframework.security.core.AuthenticationException;

public class EmailAlreadyExistsException extends AuthenticationException {
    public EmailAlreadyExistsException(String s) {
        super(s);
    }
}
