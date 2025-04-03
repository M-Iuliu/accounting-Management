package com.accounting.controller;

import com.accounting.dto.client.ClientForm;
import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.entity.Client;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.exeption.ErrorResponse;
import com.accounting.service.clients.ClientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/client")
@AllArgsConstructor
public class ClientController {

    @Autowired
    private final ClientService clientService;

    @GetMapping("/getClients")
    public ResponseEntity<?> getClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            PageDTO clientWithPagination = clientService.getClients(page, size);
            return ResponseEntity.ok(clientWithPagination);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Client not found", HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/getClientByFilters")
    public ResponseEntity<?> getClientByFilters(@RequestParam String input) {
        try {
            // Call the service method to get the ClientDTO by filters
            ClientDTO clientDTO = clientService.getClientByFilters(input);

            // Return a successful response with the ClientDTO if found
            return ResponseEntity.ok(clientDTO);

        } catch (EntityNotFoundException | ClientNotFoundException e) {
            // Handle both exceptions similarly and return a 404 response
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Client not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    @PostMapping("/saveClient")
    public ResponseEntity<Client> saveClient(@RequestBody ClientForm client){
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.saveClient(client));
    }

    @PutMapping("/updateClient/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Long id, @RequestBody ClientForm client){
        try{
            ClientDTO clientDTO = clientService.editClient(id, client);

            return ResponseEntity.ok(clientDTO);
        } catch (EntityNotFoundException | ClientNotFoundException e) {
            // If the client is not found, return a 404 status with an error message
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Client not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    @PatchMapping("/patchClient/{id}")
    public ResponseEntity<?> patchUpdateClient(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            ClientDTO updatedClient = clientService.patchClient(id, updates);
            return ResponseEntity.ok(updatedClient);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Client not found", HttpStatus.NOT_FOUND.value()));
        } catch (IllegalArgumentException | ClientNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @DeleteMapping("/deleteClient/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        try {
            // Attempt to delete the client by calling the service layer
            clientService.deleteClientById(id);

            // Return a 204 No Content status on successful deletion
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException e) {
            // If the client is not found, return a 404 status with an error message
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Client not found", HttpStatus.NOT_FOUND.value()));
        }
    }

}
