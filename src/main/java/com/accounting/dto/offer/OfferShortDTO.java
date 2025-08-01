package com.accounting.dto.offer;

import com.accounting.dto.client.ClientShortDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferShortDTO {
    private Long offerId;
    private ClientShortDTO client;
    private String destination;
    private String period;
    private double budget;
    private String obs;
}
