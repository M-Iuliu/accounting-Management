package com.accounting.dto.reservation;

import com.accounting.entity.Client;
import com.accounting.entity.Offer;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationDTO {
    private long reservationId;

    private Client client;
    private Offer offer;
    private String participants;
    private String childrenAge;
    private Date departureDate;
    private Date returnDate;
    private int personsNumber;
    private int rooms;
    private String destination;
    private String hotel;
    private String transport;
    private double totalPrice;
    private double advance;
    private double remainingCost;
    private Date paymentDeadlineDate;
    private Long providerId;
}
