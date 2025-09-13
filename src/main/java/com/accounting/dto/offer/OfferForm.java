package com.accounting.dto.offer;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OfferForm {

    private Long clientId;
    private LocalDate offerDate;
    private int adultsNb;
    private int childrenNb;
    private String destination;
    private String period;
    private double budget;
    private double grossPrice;
    private double advance;
    private double commission;
    private double acquisitionPrice;
    private String obs;

}
