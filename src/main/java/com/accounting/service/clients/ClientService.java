package com.accounting.service.clients;

import com.accounting.dto.client.ClientForm;
import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.entity.Client;
import com.accounting.exeption.ClientNotFoundException;

import java.util.Map;

public interface ClientService {
    Client saveClient(ClientForm clientAddForm);

    ClientDTO getClientByFilters(String input) throws ClientNotFoundException;

    PageDTO getClients(int page, int size);

    public Client findById(Long id);

    ClientDTO patchClient(Long id, Map<String, Object> updates) throws ClientNotFoundException;

    ClientDTO editClient(Long id, ClientForm client) throws ClientNotFoundException;

    void deleteClientById(Long id);

}
