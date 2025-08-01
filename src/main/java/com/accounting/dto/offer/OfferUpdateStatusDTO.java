package com.accounting.dto.offer;

import com.accounting.dto.reservation.ReservationForm;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferUpdateStatusDTO {

    private String status;
    private ReservationForm reservationDetails;
}
