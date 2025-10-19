package com.accounting.dto.provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderDTO {
    private Long providerId;
    private String telephone;
    private String name;
    private String webLink;
    private String contactEmail;
}
