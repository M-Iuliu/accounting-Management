package com.accounting.dto.reservation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationPatchDTO {
    private Date departureDate;
    private Date returnDate;
    private String destination;
    private String hotel;
    private Integer roomNo;
    private String transport;
    private Double price;
    private Double receipted;
    private Double balance;
    private Date balanceDueDate;
    private String currency;
    private Long providerId;
}
