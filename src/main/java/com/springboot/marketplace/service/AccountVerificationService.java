package com.springboot.marketplace.service;

import com.springboot.marketplace.exception.ConfirmationTokenException;
import com.springboot.marketplace.model.AccountVerification;
import com.springboot.marketplace.repository.AccountVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountVerificationService {
    private final AccountVerificationRepository repository;

    @Transactional
    public AccountVerification save(AccountVerification accountVerification) {
        return repository.save(accountVerification);
    }

    public AccountVerification findByVerificationToken(String verificationToken) {
        return repository.findByVerificationToken(verificationToken)
                .orElseThrow(() -> new ConfirmationTokenException("Token confirmed or expired"));
    }

    @Transactional
    public void delete(AccountVerification accountVerification) {
        repository.delete(accountVerification);
    }
}
