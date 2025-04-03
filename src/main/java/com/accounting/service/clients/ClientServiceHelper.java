package com.accounting.service.clients;

import com.accounting.dto.client.ClientForm;
import com.accounting.dto.client.ClientDTO;
import com.accounting.entity.Client;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceHelper {
    public Client mapClientForm(ClientForm input) {
        Client output = new Client();

        output.setTitle(input.getTitle());
        output.setName(input.getName());
        output.setSurname(input.getSurname());
        output.setTelephone(input.getTelephone());
        output.setEmail(input.getEmail());

        return output;
    }

    public ClientDTO mapClientToDTO(Client input) {
        ClientDTO output = new ClientDTO();

        output.setClientId(input.getClientId());
        output.setTitle(input.getTitle());
        output.setName(input.getName());
        output.setSurname(input.getSurname());
        output.setTelephone(input.getTelephone());
        output.setEmail(input.getEmail());

        return output;
    }


}
