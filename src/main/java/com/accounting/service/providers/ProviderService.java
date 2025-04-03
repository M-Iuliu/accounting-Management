package com.accounting.service.providers;

import com.accounting.dto.provider.ProviderAddForm;
import com.accounting.dto.provider.ProviderDTO;


public interface ProviderService {

    public ProviderDTO saveProvider(ProviderAddForm provider);

    ProviderDTO getProviderById(Long id);

    void deleteProviderById(Long id);

    ProviderDTO editProvider(Long id, ProviderAddForm updatedProvider);

    ProviderDTO getProviderByFilters(String input);
}
