package com.accounting.service.providers;

import com.accounting.dto.provider.ProviderAddForm;
import com.accounting.dto.provider.ProviderDTO;
import com.accounting.entity.Offer;
import com.accounting.entity.Provider;
import com.accounting.exeption.OfferNotFoundException;
import com.accounting.repository.ProviderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProviderServiceImp implements ProviderService{

    private final ProviderRepository providerRepository;
    private final ProviderServiceHelper providerServiceHelper;

    public ProviderServiceImp(ProviderRepository providerRepository, ProviderServiceHelper providerServiceHelper) {
        this.providerRepository = providerRepository;
        this.providerServiceHelper = providerServiceHelper;
    }


    public ProviderDTO saveProvider(ProviderAddForm providerAddForm) {
        // Mapping
        Provider provider = providerServiceHelper.mapProviderForm(providerAddForm);
        ProviderDTO providerDTO = providerServiceHelper.mapProviderToDTO(provider);

        // Save to the database
        provider = providerRepository.save(provider);

        // Return
        return providerDTO;
    }

    public ProviderDTO getProviderByFilters(String input) {
        Provider myProvider = providerRepository.findByFilter(input)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found with given input: " + input));

        return providerServiceHelper.mapProviderToDTO(myProvider);
    }

    public ProviderDTO getProviderById(Long id) {
        Provider provider = providerRepository.findById(id).orElse(null);

        if (provider == null){
            throw new EntityNotFoundException("Provider not found");
        }

        return providerServiceHelper.mapProviderToDTO(provider);
    }

    public void deleteProviderById(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new EntityNotFoundException("Provider with id " + id + " not found");
        }
        providerRepository.deleteById(id);
    }

    public ProviderDTO editProvider(Long id, ProviderAddForm updatedProvider) {
        // Retrieve the existing provider by ID
        Provider existingProvider = providerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found"));

        // Update the fields of the existing provider with the new data
        existingProvider.setProviderId(updatedProvider.getProviderId());
        existingProvider.setProviderName(updatedProvider.getProviderName());
        // Add other fields to update as needed

        // Save the updated provider back to the database
        Provider savedProvider = providerRepository.save(existingProvider);

        // Convert the saved entity to a DTO and return
        return providerServiceHelper.mapProviderToDTO(savedProvider);
    }

}
