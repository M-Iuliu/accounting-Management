package com.accounting.controller;

import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.provider.ProviderAddEditForm;
import com.accounting.dto.provider.ProviderDTO;
import com.accounting.exeption.ErrorResponse;
import com.accounting.service.providers.ProviderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/provider")
@AllArgsConstructor
public class ProviderController {

    @Autowired
    private final ProviderService providerService;

    @PostMapping()
    public ResponseEntity<ProviderDTO> saveProvider(@RequestBody ProviderAddEditForm provider) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(providerService.saveProvider(provider));
    }

    @GetMapping()
    public ResponseEntity<?> getProviderByFilters(@RequestParam String input,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "5") int size) {
        try {
            PageDTO providerDTO = providerService.getProviderByFilters(input, page, size);

            return ResponseEntity.ok(providerDTO);

        } catch (EntityNotFoundException e) {
            // Handle both exceptions similarly and return a 404 response
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Provider not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProvider(@PathVariable Long id){
        try {
            ProviderDTO provider = providerService.getProviderDtoById(id);

            return ResponseEntity.ok(provider);

        } catch (EntityNotFoundException e) {
            // If the provider is not found, return a 404 status with an error message
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Provider not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProvider(@PathVariable Long id) {
        try {
            providerService.deleteProviderById(id);

            // Return a 204 No Content status on successful deletion
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException e) {
            // If the provider is not found, return a 404 status with an error message
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Provider not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editProvider(@PathVariable Long id, @RequestBody ProviderAddEditForm updatedProvider) {
        try {
            ProviderDTO updatedProviderDTO = providerService.editProvider(id, updatedProvider);

            return ResponseEntity.ok(updatedProviderDTO);

        } catch (EntityNotFoundException e) {
            // If the provider is not found, return a 404 status with an error message
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Provider not found", HttpStatus.NOT_FOUND.value()));
        }
    }

}
