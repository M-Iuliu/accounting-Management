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
    private Integer adultsNo;
    private Integer childrenNo;
    private String destination;
    private String period;
    private String currency;
    private Double budget;
    private Double grossPrice;
    private Double advance;
    private Double commission;
    private Double acquisitionPrice;
}
