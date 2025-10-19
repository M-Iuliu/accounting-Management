package com.accounting.dto.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientShortDTO {
    private Long clientId;
    private String title;
    private String fullName;
    private String telephone;
}
