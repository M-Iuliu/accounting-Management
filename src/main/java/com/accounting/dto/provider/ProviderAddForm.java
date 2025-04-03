package com.accounting.dto.provider;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProviderAddForm {
    private Long providerId;
    private String telephone;
    private String providerName;
}
