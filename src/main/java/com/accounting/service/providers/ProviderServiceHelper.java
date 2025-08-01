package com.accounting.service.providers;

import com.accounting.dto.provider.ProviderAddEditForm;
import com.accounting.dto.provider.ProviderDTO;
import com.accounting.entity.Provider;
import org.springframework.stereotype.Service;

@Service
public class ProviderServiceHelper {

    public static Provider mapProviderForm(ProviderAddEditForm input) {
       Provider output = new Provider();

        output.setProviderName(input.getProviderName() != null ? input.getProviderName() : null);
        output.setTelephone(input.getTelephone() != null ? input.getTelephone() : null);
        output.setEmail(input.getEmail() != null ? input.getEmail() : null);
        output.setWebLink(input.getWebLink() != null ? input.getWebLink() : null);

       return output;
   }

    public static ProviderDTO mapProviderToDTO(Provider provider) {
       ProviderDTO output = new ProviderDTO();

       output.setProviderId(provider.getProviderId());
        output.setProviderName(provider.getProviderName() != null ? provider.getProviderName() : null);
        output.setTelephone(provider.getTelephone() != null ? provider.getTelephone() : null);
        output.setEmail(provider.getEmail() != null ? provider.getEmail() : null);
        output.setWebLink(provider.getWebLink() != null ? provider.getWebLink() : null);
       return output;
   }

    public static void updateProvider(ProviderAddEditForm updatedProvider, Provider existingProvider) {
        if (updatedProvider.getProviderName() != null) {
            existingProvider.setProviderName(updatedProvider.getProviderName());
        }
        if (updatedProvider.getTelephone() != null) {
            existingProvider.setTelephone(updatedProvider.getTelephone());
        }
        if (updatedProvider.getEmail() != null) {
            existingProvider.setEmail(updatedProvider.getEmail());
        }
        if (updatedProvider.getWebLink() != null) {
            existingProvider.setWebLink(updatedProvider.getWebLink());
        }
    }



}
