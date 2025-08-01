package com.accounting.service.providers;

import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.provider.ProviderAddEditForm;
import com.accounting.dto.provider.ProviderDTO;
import com.accounting.entity.Provider;


public interface ProviderService {

    ProviderDTO saveProvider(ProviderAddEditForm provider);

    Provider findById(Long id);

    ProviderDTO getProviderDtoById(Long id);

    void deleteProviderById(Long id);

    ProviderDTO editProvider(Long id, ProviderAddEditForm updatedProvider);

    PageDTO getProviderByFilters(String input, int page, int size);

    ProviderDTO mapToDto(Provider provider);
}
