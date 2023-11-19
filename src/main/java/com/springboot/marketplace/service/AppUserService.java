package com.springboot.marketplace.service;

import com.springboot.marketplace.exception.EmailAlreadyExistsException;
import com.springboot.marketplace.exception.PasswordsNotMatchException;
import com.springboot.marketplace.model.AccountVerification;
import com.springboot.marketplace.model.AppUser;
import com.springboot.marketplace.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AppUserService implements UserDetailsService {
    private final AppUserRepository appUserRepository;
    private final AccountVerificationService accountVerificationService;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void save(AppUser appUser) {
        if (!appUser.getPassword().equals(appUser.getConfirmPassword())) {
            throw new PasswordsNotMatchException("Senhas não conferem.");
        }

        if (appUserRepository.existsByEmail(appUser.getEmail()))
            throw new EmailAlreadyExistsException("Email já existe");

        String encryptedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encryptedPassword);

        AppUser appUserSaved = appUserRepository.save(appUser);
        AccountVerification accountVerification = accountVerificationService.save(new AccountVerification(appUserSaved));

        String emailBody = String.format(
                "<p>Para ativar sua conta, clique no link abaixo:</p>" +
                        "<a href=\"%s\">Ativar sua conta</a>",
                "http://localhost:8081/auth/confirm?verificationToken=" + accountVerification.getVerificationToken());
        emailSenderService.sendEmail(appUser.getEmail(), "Confirmação de Email", emailBody);
    }

    @Transactional
    public void enableAppUser(String verificationToken) {
        AccountVerification accountVerification = accountVerificationService.findByVerificationToken(verificationToken);
        AppUser appUser = accountVerification.getAppUser();

        appUser.setEnabled(true);
        appUserRepository.save(appUser);
        accountVerificationService.delete(accountVerification);
    }

    @Async
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteByIsEnabledFalse() {
        appUserRepository.deleteByIsEnabledFalse();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(username).orElseThrow();
    }
}
