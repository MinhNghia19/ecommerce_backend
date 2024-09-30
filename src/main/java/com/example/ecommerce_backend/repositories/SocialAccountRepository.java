package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.models.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {


//    boolean existsByProviderId(String providerId);
Optional<SocialAccount> findByProviderAndProviderId(String provider, String providerId);
}
