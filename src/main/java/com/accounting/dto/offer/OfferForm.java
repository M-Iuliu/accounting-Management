package com.accounting.dto.offer;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OfferForm {

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @PastOrPresent(message = "Offer date cannot be in the future")
    private LocalDate offerDate;

    @Min(value = 0, message = "Number of adults cannot be negative")
    @Max(value = 50, message = "Number of adults cannot exceed 50")
    private int adultsNo;

    @Min(value = 0, message = "Number of children cannot be negative")
    @Max(value = 50, message = "Number of children cannot exceed 50")
    private int childrenNo;

    @NotBlank(message = "Destination is required")
    @Size(max = 255, message = "Destination must not exceed 255 characters")
    private String destination;

    @Size(max = 255, message = "Period must not exceed 255 characters")
    private String period;

    @Min(value = 0, message = "Budget cannot be negative")
    private double budget;

    @Min(value = 0, message = "Gross price cannot be negative")
    private double grossPrice;

    @Min(value = 0, message = "Advance cannot be negative")
    private double advance;

    @Min(value = 0, message = "Commission cannot be negative")
    private double commission;

    @Min(value = 0, message = "Acquisition price cannot be negative")
    private double acquisitionPrice;

    @Size(max = 500, message = "Observations must not exceed 500 characters")
    private String obs;

}
