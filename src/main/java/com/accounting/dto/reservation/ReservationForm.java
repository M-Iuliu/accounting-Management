package com.accounting.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationForm {

    private List<ReservationParticipantDTO> participants;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private int personsNumber;
    private int roomNo;
    private String destination;
    private String hotel;
    private String transport;
    private double totalPrice;
    private double advance;
    private double remainingCost;
    private String currency;
    private LocalDate paymentDeadlineDate;
    private Long providerId;
}
