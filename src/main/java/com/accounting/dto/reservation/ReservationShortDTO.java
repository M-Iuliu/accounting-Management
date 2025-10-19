package com.accounting.dto.reservation;

import com.accounting.dto.client.ClientShortDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationShortDTO { // only fields required for UI table display
    private Long reservationId;
    private ClientShortDTO client;
    private String bookingRef;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private int personNo;
    private int roomNo;
    private double price;
    private double receipted;
    private double balance;
    private LocalDate balanceDueDate;
    private String currency;
}
