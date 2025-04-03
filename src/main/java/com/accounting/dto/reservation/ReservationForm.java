package com.accounting.dto.reservation;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReservationForm {

    @NotNull
    private Long clientId;

    @NotNull
    private Long offerId;

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
