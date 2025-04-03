package com.accounting.dto.provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderDTO {
    private Long providerId;
    private String telephone;
    private String providerName;
}
