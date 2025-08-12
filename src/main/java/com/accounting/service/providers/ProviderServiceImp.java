package com.accounting.service.providers;

import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.dto.provider.ProviderAddEditForm;
import com.accounting.dto.provider.ProviderDTO;
import com.accounting.entity.Provider;
import com.accounting.repository.ProviderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.accounting.service.providers.ProviderServiceHelper.mapProviderToDTO;
import static com.accounting.service.providers.ProviderServiceHelper.updateProvider;

@Service
public class ProviderServiceImp implements ProviderService{

    private final ProviderRepository providerRepository;

    public ProviderServiceImp(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public ProviderDTO saveProvider(ProviderAddEditForm providerAddEditForm) {
        Provider provider = ProviderServiceHelper.mapProviderForm(providerAddEditForm);
        return mapProviderToDTO(providerRepository.save(provider));
    }

    public Provider findById(Long id) {
        return providerRepository.findById(id).orElseThrow((() -> new EntityNotFoundException("Provider not found")));
    }

    public ProviderDTO getProviderDtoById(Long id) {
        return mapProviderToDTO(findById(id));
    }

    public PageDTO getProviderByFilters(String input, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Provider> providerPage;
        if (input == null || input.isBlank()) {
            providerPage = providerRepository.findAllActiveProviders(pageable);
        } else {
            providerPage = providerRepository.findByFilter(input.trim(), pageable);
        }

        List<ProviderDTO> providerDTOList = providerPage.getContent()
                .stream()
                .map(ProviderServiceHelper::mapProviderToDTO)
                .toList();

        PaginationDTO pagination = new PaginationDTO(
                providerPage.getTotalElements(),
                providerPage.getSize(),
                List.of(5, 10, 20),
                providerPage.getNumber()
        );

        return new PageDTO<>(providerDTOList, pagination);
    }

    public ProviderDTO editProvider(Long id, ProviderAddEditForm updateDto) {
        Provider provider = findById(id);
        updateProvider(updateDto, provider);
        return mapProviderToDTO(providerRepository.save(provider));
    }

    public void deleteProviderById(Long id) {
        Provider provider = findById(id);
        provider.setIsDeleted(Boolean.TRUE);
        provider.setDeletionDate(new Date());
        providerRepository.save(provider);
    }

    public ProviderDTO mapToDto(Provider provider) {
        return mapProviderToDTO(provider);
    }

}
