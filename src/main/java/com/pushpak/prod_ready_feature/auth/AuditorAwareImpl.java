package com.pushpak.prod_ready_feature.auth;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // get security context
        // get authenticated user
        // get principle
        // get usernam e form the principle
        return Optional.of("Pushpak Jangela");
    }
}
