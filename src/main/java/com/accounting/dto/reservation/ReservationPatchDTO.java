package com.accounting.dto.reservation;

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
public class ReservationPatchDTO {
    private LocalDate departureDate;
    private LocalDate returnDate;
    private String destination;
    private String hotel;
    private Integer roomNo;
    private String transport;
    private Double price;
    private Double receipted;
    private Double balance;
    private LocalDate balanceDueDate;
    private String currency;
    private Long providerId;
}
