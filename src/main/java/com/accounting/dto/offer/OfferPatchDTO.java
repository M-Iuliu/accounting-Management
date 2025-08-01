package com.accounting.dto.offer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferPatchDTO {
    //TODO: Check if client can be changed on edit offer
    private Integer adultsNb;
    private Integer childrenNb;
    private String destination;
    private String period;
    private Double budget;
    private Double grossPrice;
    private Double advance;
    private Double commission;
    private Double acquisitionPrice;
}
