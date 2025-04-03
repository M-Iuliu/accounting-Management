package com.accounting.dto.offer;

import com.accounting.constants.OfferStatusEnum;
import com.accounting.entity.Client;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferDTO {
    private Long offerId;
    private Client client;
    private int adultsNb;
    private int childrenNb;
    private String destination;
    private String period;
    private double budget;
    private double grossPrice;
    private double advance;
    private double commission;
    private double acquisitionPrice;

    @Enumerated(EnumType.STRING)
    private OfferStatusEnum status;

    private String obs;

}
