package com.accounting.dto.reservation;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
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

    @Size(max = 255, message = "Destination must not exceed 255 characters")
    private String destination;

    @Size(max = 255, message = "Hotel must not exceed 255 characters")
    private String hotel;

    @Min(value = 1, message = "Number of rooms must be at least 1")
    @Max(value = 20, message = "Number of rooms cannot exceed 20")
    private Integer roomNo;

    @Size(max = 100, message = "Transport type must not exceed 100 characters")
    private String transportType;

    @Min(value = 0, message = "Price cannot be negative")
    private Double price;

    @Min(value = 0, message = "Receipted cannot be negative")
    private Double receipted;

    @Min(value = 0, message = "Balance cannot be negative")
    private Double balance;

    private LocalDate balanceDueDate;

    @Size(max = 10, message = "Currency must not exceed 10 characters")
    private String currency;

    private Long providerId;
}
