package com.accounting.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReservationDTO {
    private long reservationId;

    private String name;
    private List<String> participantsList;
    private List<Integer> childrenAgeList;
    private Date departureDate;
    private Date returnDate;
    private int personsNumber;
    private int rooms;
    private String telephone;
    private String destination;
    private String hotel;
    private String transport;
    private double totalPrice;
    private double advance;
    private double remainingCost;
    private Date paymentDeadlineDate;
    private Long providerId;
}
