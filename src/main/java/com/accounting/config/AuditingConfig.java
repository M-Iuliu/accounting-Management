package com.accounting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Configuration class to enable JPA auditing functionality.
 * Automatically populates audit fields (createdBy, createdDate, modifiedBy, modifiedDate)
 * on entities that extend BaseAuditableEntity.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    /**
     * Simple implementation that returns SYSTEM as auditor.
     * TODO: Replace with Spring Security context when authentication is implemented
     * e.g., SecurityContextHolder.getContext().getAuthentication().getName()
     */
    static class AuditorAwareImpl implements AuditorAware<String> {
        @Override
        public Optional<String> getCurrentAuditor() {
            // TODO: When Spring Security is added, replace with actual user
            return Optional.of("SYSTEM");
        }
    }
}
