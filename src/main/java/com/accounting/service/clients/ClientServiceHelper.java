package com.accounting.service.clients;

import com.accounting.dto.client.ClientAddEditForm;
import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.client.ClientShortDTO;
import com.accounting.entity.Client;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceHelper {
    public static Client mapClientForm(ClientAddEditForm input) {
        Client output = new Client();

        output.setTitle(input.getTitle());
        output.setName(input.getName());
        output.setSurname(input.getSurname());
        output.setTelephone(input.getTelephone());
        output.setEmail(input.getEmail());

        return output;
    }

    public static ClientDTO mapClientToDTO(Client input) {
        ClientDTO output = new ClientDTO();

        output.setClientId(input.getClientId());
        output.setTitle(input.getTitle());
        output.setName(input.getName());
        output.setSurname(input.getSurname());
        output.setTelephone(input.getTelephone());
        output.setEmail(input.getEmail());
        output.setObs(input.getObs());

        return output;
    }

    public static ClientShortDTO mapClientToShortDTO(Client input) {
        ClientShortDTO output = new ClientShortDTO();
        output.setClientId(input.getClientId());
        output.setTitle(input.getTitle());
        output.setFullName(input.getFullName());
        output.setTelephone(input.getTelephone());

        return output;
    }
    
    public static void patchClient(ClientAddEditForm patch, Client client) {
        if (patch.getTitle() != null) client.setTitle(patch.getTitle());
        if (patch.getSurname() != null) client.setSurname(patch.getSurname());
        if (patch.getName() != null) client.setName(patch.getName());
        if (patch.getTelephone() != null) client.setTelephone(patch.getTelephone());
        if (patch.getEmail() != null) client.setEmail(patch.getEmail());
        if (patch.getObs() != null) client.setObs(patch.getObs());
    }


}
