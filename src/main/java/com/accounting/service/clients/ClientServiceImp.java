package com.accounting.service.clients;

import com.accounting.dto.client.ClientAddEditForm;
import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.entity.Client;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.mapper.ClientMapper;
import com.accounting.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Service implementation for Client domain operations.
 * Handles business logic, transaction management, and orchestrates
 * interactions between controller, repository, and mapper layers.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImp implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ClientServiceHelper clientServiceHelper;

    /**
     * Finds a client by ID, ensuring it's not deleted
     * @param id the client ID
     * @return the found client
     * @throws ClientNotFoundException if client not found or deleted
     */
    @Override
    @Transactional(readOnly = true)
    public Client findById(Long id) throws ClientNotFoundException {
        log.info("Finding client by ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Client not found with ID: {}", id);
                    return new ClientNotFoundException("Client not found with ID: " + id);
                });

        if (client.getIsDeleted() != null && client.getIsDeleted()) {
            log.warn("Attempt to access deleted client with ID: {}", id);
            throw new ClientNotFoundException("Client with ID " + id + " is inactive");
        }

        log.debug("Successfully found client: {}", client.getClientId());
        return client;
    }

    /**
     * Retrieves paginated list of clients with optional filtering
     * @param input search filter (searches name, surname, telephone)
     * @param page page number (0-indexed)
     * @param size page size
     * @return paginated client list with metadata
     */
    @Override
    @Transactional(readOnly = true)
    public PageDTO getClients(String input, int page, int size) {
        log.info("Getting clients - page: {}, size: {}, filter: '{}'", page, size, input);

        Pageable pageable = PageRequest.of(page, size);
        Page<Client> clientPage;

        if (input == null || input.isBlank()) {
            log.debug("Retrieving all active clients");
            clientPage = clientRepository.findAllActiveClients(pageable);
        } else {
            log.debug("Filtering clients by input: {}", input);
            clientPage = clientRepository.findByFilter(input.trim(), pageable);
        }

        List<ClientDTO> clientsList = clientPage.getContent()
                .stream()
                .map(clientServiceHelper::mapClientToDTO)
                .toList();

        PaginationDTO pagination = new PaginationDTO(
                clientPage.getTotalElements(),
                clientPage.getSize(),
                List.of(5, 10, 20),
                clientPage.getNumber()
        );

        log.info("Retrieved {} clients out of {} total", clientsList.size(), clientPage.getTotalElements());
        return new PageDTO<>(clientsList, pagination);
    }

    /**
     * Creates a new client
     * @param clientAddForm the form data for new client
     * @return the created client entity
     */
    @Override
    @Transactional
    public Client saveClient(ClientAddEditForm clientAddForm) {
        log.info("Creating new client: {} {}", clientAddForm.getName(), clientAddForm.getSurname());

        Client client = clientMapper.toEntity(clientAddForm);
        clientServiceHelper.validateClient(client);
        client = clientRepository.save(client);

        log.info("Successfully created client with ID: {}", client.getClientId());
        return client;
    }

    /**
     * Updates an existing client
     * @param id the client ID to update
     * @param clientAddEditForm the form data with updates
     * @return the updated client as DTO
     * @throws ClientNotFoundException if client not found
     */
    @Override
    @Transactional
    public ClientDTO editClient(Long id, ClientAddEditForm clientAddEditForm) throws ClientNotFoundException {
        log.info("Updating client with ID: {}", id);

        Client client = findById(id);
        clientMapper.updateEntityFromForm(clientAddEditForm, client);
        clientServiceHelper.validateClient(client);
        client = clientRepository.save(client);

        log.info("Successfully updated client with ID: {}", id);
        return clientServiceHelper.mapClientToDTO(client);
    }

    /**
     * Soft-deletes a client by ID
     * @param id the client ID to delete
     * @throws ClientNotFoundException if client not found
     */
    @Override
    @Transactional
    public void deleteClientById(Long id) throws ClientNotFoundException {
        log.info("Deleting client with ID: {}", id);

        Client client = findById(id);
        client.setIsDeleted(Boolean.TRUE);
        client.setDeletionDate(new Date());
        clientRepository.save(client);

        log.info("Successfully soft-deleted client with ID: {}", id);
    }

    /**
     * Maps client entity to DTO
     * @param client the client entity
     * @return ClientDTO representation
     */
    @Override
    @Transactional(readOnly = true)
    public ClientDTO mapToDto(Client client) {
        return clientServiceHelper.mapClientToDTO(client);
    }

    /**
     * Maps client entity to short DTO
     * @param client the client entity
     * @return ClientShortDTO representation
     */
    @Override
    @Transactional(readOnly = true)
    public ClientShortDTO mapToShortDto(Client client) {
        return clientServiceHelper.mapClientToShortDTO(client);
    }
}
