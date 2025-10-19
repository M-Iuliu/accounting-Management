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
public class OfferDTO {
    private Long offerId;
    private ClientShortDTO client;
    private int adultsNo;
    private int childrenNo;
    private String destination;
    private String period;
    private String currency;
    private double budget;
    private double grossPrice;
    private double advance;
    private double commission;
    private double acquisitionPrice;

    @Enumerated(EnumType.STRING)
    private OfferStatusEnum status;

    private String obs;

}
