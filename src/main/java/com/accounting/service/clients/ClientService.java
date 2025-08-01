package com.accounting.service.clients;

import com.accounting.dto.client.ClientAddEditForm;
import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.entity.Client;
import com.accounting.exeption.ClientNotFoundException;

public interface ClientService {
    Client saveClient(ClientAddEditForm clientAddForm);

    PageDTO getClients(String input, int page, int size);

    Client findById(Long id) throws ClientNotFoundException;

    ClientDTO patchClient(Long id, ClientAddEditForm updateDto) throws ClientNotFoundException;

    void deleteClientById(Long id) throws ClientNotFoundException;

    ClientDTO mapToDto(Client client);

    ClientShortDTO mapToShortDto(Client client);
}
