package com.accounting.service.clients;

import com.accounting.dto.client.ClientForm;
import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.entity.Client;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.repository.ClientRepository;
import com.accounting.repository.OfferRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClientServiceImp implements ClientService{

    private final ClientServiceHelper clientServiceHelper;
    private final ClientRepository clientRepository;
    private final OfferRepository offerRepository;

    public ClientServiceImp(ClientServiceHelper clientServiceHelper, ClientRepository clientRepository, OfferRepository offerRepository) {
        this.clientServiceHelper = clientServiceHelper;
        this.clientRepository = clientRepository;
        this.offerRepository = offerRepository;
    }

    public ClientDTO getClientByFilters(String input) throws ClientNotFoundException {
        Client myClient = clientRepository.findByFilter(input)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with given input: " + input));

        return clientServiceHelper.mapClientToDTO(myClient);
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + id));
    }

    public PageDTO getClients(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Client> clientPage = clientRepository.findAll(pageable);

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

        return new PageDTO<>(clientsList, pagination);
    }

    public Client saveClient(ClientForm clientAddForm) {
        // Map form to entity
        Client client = clientServiceHelper.mapClientForm(clientAddForm);

        // Save to the database
        client = clientRepository.save(client);

        // Return
        return client;
    }

    public ClientDTO editClient(Long id, ClientForm clientForm) throws ClientNotFoundException {
        // get client from DB
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));

        //map client to DTO entity
        client = clientServiceHelper.mapClientForm(clientForm);

        // Save to the database
        client = clientRepository.save(client);

        // Return
        return clientServiceHelper.mapClientToDTO(client);
    }

    public ClientDTO patchClient(Long id, Map<String, Object> updates)  throws ClientNotFoundException {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));

        Set<String> inputKeys = updates.keySet();
        Set<String> clientKeys = Arrays.stream(Client.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());

        for (String key : inputKeys) {
            if (clientKeys.contains(key)) {
                try {
                    // Find the setter method (e.g., setName for "name")
                    Method setter = Client.class.getMethod("set" + Character.toUpperCase(key.charAt(0)) + key.substring(1), String.class);

                    // Invoke the setter on the existing client object
                    setter.invoke(client, updates.get(key));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to set field: " + key, e);
                }
            }
        }

        clientRepository.save(client);

        return clientServiceHelper.mapClientToDTO(client);
    }

    public void deleteClientById(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new EntityNotFoundException("Client with id " + id + " not found");
        }
        clientRepository.deleteById(id);
    }

}
