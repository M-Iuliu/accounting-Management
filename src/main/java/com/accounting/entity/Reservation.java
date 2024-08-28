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
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    private Long clientId;
    private String participants;
    private String childrenAge;
    private Date departureDate;
    private Date returnDate;
    private int rooms;
    private String destination;
    private String hotel;
    private String transport;
    private double totalPrice;
    private double remainingCost;
    private Date paymentDeadlineDate;
    private Long providerId;

}
