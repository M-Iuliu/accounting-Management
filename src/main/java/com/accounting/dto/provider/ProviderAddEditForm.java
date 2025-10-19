package com.accounting.dto.provider;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProviderAddEditForm {
    private String telephone;
    private String providerName;
    private String email;
    private String webLink;
}
