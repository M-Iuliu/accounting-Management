package com.accounting.entity;

import com.accounting.constants.OfferStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "client")
@EqualsAndHashCode(callSuper = false)
public class Offer extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offer_id")
    private Long offerId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "offer_date")
    private LocalDate offerDate;

    @Column(name = "adults_no")
    private int adultsNo;

    @Column(name = "children_no")
    private int childrenNo;

    @Column(length = 255)
    private String destination;

    @Column(length = 255)
    private String period;

    @Column(length = 10)
    private String currency;

    private double budget;

    @Column(name = "gross_price")
    private double grossPrice;

    private double advance;

    private double commission;

    @Column(name = "acquisition_price")
    private double acquisitionPrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private OfferStatusEnum status;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deletion_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletionDate;

    @Column(length = 500)
    private String obs;

}
