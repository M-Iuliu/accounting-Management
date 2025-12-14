package com.accounting.dto.offer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferPatchDTO {
    @Min(value = 0, message = "Number of adults cannot be negative")
    @Max(value = 50, message = "Number of adults cannot exceed 50")
    private Integer adultsNo;

    @Min(value = 0, message = "Number of children cannot be negative")
    @Max(value = 50, message = "Number of children cannot exceed 50")
    private Integer childrenNo;

    @Size(max = 255, message = "Destination must not exceed 255 characters")
    private String destination;

    @Size(max = 255, message = "Period must not exceed 255 characters")
    private String period;

    @Size(max = 10, message = "Currency must not exceed 10 characters")
    private String currency;

    @Min(value = 0, message = "Budget cannot be negative")
    private Double budget;

    @Min(value = 0, message = "Gross price cannot be negative")
    private Double grossPrice;

    @Min(value = 0, message = "Advance cannot be negative")
    private Double advance;

    @Min(value = 0, message = "Commission cannot be negative")
    private Double commission;

    @Min(value = 0, message = "Acquisition price cannot be negative")
    private Double acquisitionPrice;
}
