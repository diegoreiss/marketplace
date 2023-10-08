package com.springboot.marketplace.repository;

import com.springboot.marketplace.model.AccountVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountVerificationRepository extends JpaRepository<AccountVerification, String> {
    Optional<AccountVerification> findByVerificationToken(String verificationToken);
}
