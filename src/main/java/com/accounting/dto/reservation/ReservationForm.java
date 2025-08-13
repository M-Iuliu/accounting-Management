package com.accounting.dto.reservation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationForm {

    @NotNull
    private Long clientId;

    @NotNull
    private Long offerId;

    private List<ReservationParticipantDTO> participants;
    private Date departureDate;
    private Date returnDate;
    private int personsNumber;
    private int roomNo;
    private String destination;
    private String hotel;
    private String transport;
    private double totalPrice;
    private double advance;
    private double remainingCost;
    private String currency;
    private Date paymentDeadlineDate;
    private Long providerId;
}
