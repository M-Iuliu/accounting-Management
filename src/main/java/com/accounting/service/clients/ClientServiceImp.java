package com.accounting.service.clients;

import com.accounting.dto.client.ClientAddEditForm;
import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.entity.Client;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.repository.ClientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ClientServiceImp implements ClientService{

    private final ClientRepository clientRepository;

    public ClientServiceImp(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client findById(Long id) throws ClientNotFoundException {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with ID: " + id));

        //TODO: refactor repo getByIdIfNotDeleted?
        if(client.getDeletionDate() != null) {
            throw new ClientNotFoundException("Client with ID " + id + " is inactive");
        }

        return client;
    }

    public PageDTO getClients(String input, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Client> clientPage;
        if (input == null || input.isBlank()) {
            clientPage = clientRepository.findAllActiveClients(pageable);
        } else {
            clientPage = clientRepository.findByFilter(input.trim(), pageable);
        }

        List<ClientDTO> clientsList = clientPage.getContent()
                .stream()
                .map(ClientServiceHelper::mapClientToDTO)
                .toList();

        PaginationDTO pagination = new PaginationDTO(
                clientPage.getTotalElements(),
                clientPage.getSize(),
                List.of(5, 10, 20),
                clientPage.getNumber()
        );

        return new PageDTO<>(clientsList, pagination);
    }

    public Client saveClient(ClientAddEditForm clientAddForm) {
        // Map form to entity
        Client client = ClientServiceHelper.mapClientForm(clientAddForm);

        // Save to the database
        client = clientRepository.save(client);

        // Return
        return client;
    }

    public ClientDTO editClient(Long id, ClientAddEditForm clientAddEditForm) throws ClientNotFoundException {
        // get client from DB
        Client client = findById(id);

        //map client to DTO entity
        ClientServiceHelper.patchClient(clientAddEditForm, client);

        // Return
        return ClientServiceHelper.mapClientToDTO(clientRepository.save(client));
    }

    public void deleteClientById(Long id) throws ClientNotFoundException {
        Client client = findById(id);
        client.setIsDeleted(Boolean.TRUE);
        client.setDeletionDate(new Date());
        clientRepository.save(client);
    }

    public ClientDTO mapToDto(Client client){
        return ClientServiceHelper.mapClientToDTO(client);
    }

    public ClientShortDTO mapToShortDto(Client client){
        return ClientServiceHelper.mapClientToShortDTO(client);
    }

}
