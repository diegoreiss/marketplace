package com.springboot.marketplace.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity(name = "tb_account_verification")
@Table(name = "tb_account_verification")
public class AccountVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String verificationToken;

    @ManyToOne
    @JoinColumn(nullable = false, name = "tb_app_user_id")
    private AppUser appUser;

    public AccountVerification(AppUser appUser) {
        this.verificationToken = UUID.randomUUID().toString();
        this.appUser = appUser;
    }
}
