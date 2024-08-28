package com.accounting.dto;

import com.accounting.entity.Observation;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OfferDTO {
    private Long offerId;

    private String title;
    private String name;
    private String surname;
    private int adultsNb;
    private int childrenNb;
    private String destination;
    private String period;
    private double budget;
    private double grossPrice;
    private double advance;
    private double commission;
    private double acquisitionPrice;
    private Enum status;
    private List<Observation> obs;

}
