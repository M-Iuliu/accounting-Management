package com.accounting.service.providers;

import com.accounting.dto.provider.ProviderAddForm;
import com.accounting.dto.provider.ProviderDTO;
import com.accounting.entity.Provider;
import org.springframework.stereotype.Service;

@Service
public class ProviderServiceHelper {

   public Provider mapProviderForm(ProviderAddForm input){
       Provider output = new Provider();

       output.setProviderId(input.getProviderId());
       output.setProviderName(input.getProviderName());
       output.setTelephone(input.getTelephone());

       return output;
   }

   public ProviderDTO mapProviderToDTO(Provider provider){
       ProviderDTO output = new ProviderDTO();

       output.setProviderId(provider.getProviderId());
       output.setProviderName(provider.getProviderName());
       output.setTelephone(provider.getTelephone());

       return output;
   }


}
