package com.accounting.dto.offer;

import com.accounting.constants.OfferStatusEnum;
import com.accounting.dto.client.ClientShortDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    private OfferStatusEnum status;
    private String obs;
}
