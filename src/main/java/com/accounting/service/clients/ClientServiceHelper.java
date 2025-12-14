package com.accounting.service.clients;

import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.client.ClientShortDTO;
import com.accounting.entity.Client;
import com.accounting.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Helper component for Client service operations.
 * Provides utility methods and delegates to ClientMapper for entity-DTO conversions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClientServiceHelper {

    private final ClientMapper clientMapper;

    /**
     * Maps Client entity to ClientDTO
     * @param client the client entity
     * @return ClientDTO representation
     */
    public ClientDTO mapClientToDTO(Client client) {
        log.debug("Mapping Client entity to DTO for client ID: {}", client.getClientId());
        return clientMapper.toDTO(client);
    }

    /**
     * Maps Client entity to ClientShortDTO
     * @param client the client entity
     * @return ClientShortDTO representation
     */
    public ClientShortDTO mapClientToShortDTO(Client client) {
        log.debug("Mapping Client entity to ShortDTO for client ID: {}", client.getClientId());
        return clientMapper.toShortDTO(client);
    }

    /**
     * Validates client business rules before save/update
     * @param client the client to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateClient(Client client) {
        log.debug("Validating client: {}", client.getClientId());

        if (client.getName() != null && client.getSurname() != null) {
            String fullName = client.getFullName();
            if (fullName.length() > 200) {
                throw new IllegalArgumentException("Full name exceeds maximum length");
            }
        }

        log.debug("Client validation successful");
    }
}
