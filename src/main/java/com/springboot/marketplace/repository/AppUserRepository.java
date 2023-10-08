package com.springboot.marketplace.repository;

import com.springboot.marketplace.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {
    Optional<UserDetails> findByEmail(String email);

    Boolean existsByEmail(String email);

    void deleteByIsEnabledFalse();
}
