package com.springboot.marketplace.exception;

import org.springframework.security.core.AuthenticationException;

public class ConfirmationTokenException extends AuthenticationException {
    public ConfirmationTokenException(String msg) {
        super(msg);
    }
}
