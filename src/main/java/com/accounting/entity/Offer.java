package com.accounting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offerId;

    private Long clientId;
    private Date offerDate;
    private String title;
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
    private Long obsId;


}
