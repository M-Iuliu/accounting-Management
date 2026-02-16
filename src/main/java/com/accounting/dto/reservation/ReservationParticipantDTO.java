package com.accounting.dto.reservation;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationParticipantDTO {

    private Long reservationId;

    @NotBlank(message = "Participant name is required")
    @Size(min = 2, max = 200, message = "Participant name must be between 2 and 200 characters")
    private String participantName;

    @NotNull(message = "Participant age is required")
    @Min(value = 0, message = "Participant age cannot be negative")
    @Max(value = 120, message = "Participant age cannot exceed 120")
    private Long participantAge;

}
