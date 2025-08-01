package com.accounting.dto.reservation;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReservationParticipantDTO {

    private Long reservationId;
    private String participantName;
    private Long participantAge;

}
