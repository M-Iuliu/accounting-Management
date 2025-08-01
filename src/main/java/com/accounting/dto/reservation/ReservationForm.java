package com.accounting.dto.reservation;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

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

    private List<ReservationParticipantDTO> participants;
    //    private String childrenAge;
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
    private Date paymentDeadlineDate;
    private Long providerId;

}
