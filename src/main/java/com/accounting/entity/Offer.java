package com.accounting.entity;

import com.accounting.constants.OfferStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "client")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offerId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    private Date offerDate;
    private int adultsNo;
    private int childrenNo;
    //    private int personsNumber;
    private String destination;
    private String period;
    private double budget;
    private double grossPrice;
    private double advance;
    private double commission;
    private double acquisitionPrice;

    @Enumerated(EnumType.STRING)
    private OfferStatusEnum status;

    private Boolean isDeleted;
    private Date deletionDate; //TODO: to add Auditable to all classes

    private String obs;

}
