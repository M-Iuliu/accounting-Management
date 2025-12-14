package com.accounting.dto.reservation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationForm {

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @PastOrPresent(message = "Booked date cannot be in the future")
    private LocalDate bookedDate;

    @NotNull(message = "Participants list is required")
    @NotEmpty(message = "At least one participant is required")
    @Valid
    private List<ReservationParticipantDTO> participants;

    @NotNull(message = "Departure date is required")
    @Future(message = "Departure date must be in the future")
    private LocalDate departureDate;

    @NotNull(message = "Return date is required")
    private LocalDate returnDate;

    @Min(value = 1, message = "Number of persons must be at least 1")
    @Max(value = 50, message = "Number of persons cannot exceed 50")
    private int personsNumber;

    @Min(value = 1, message = "Number of rooms must be at least 1")
    @Max(value = 20, message = "Number of rooms cannot exceed 20")
    private int roomNo;

    @NotBlank(message = "Destination is required")
    @Size(max = 255, message = "Destination must not exceed 255 characters")
    private String destination;

    @Size(max = 255, message = "Hotel must not exceed 255 characters")
    private String hotel;

    @Size(max = 100, message = "Transport must not exceed 100 characters")
    private String transport;

    @Min(value = 0, message = "Total price cannot be negative")
    private double totalPrice;

    @Min(value = 0, message = "Advance cannot be negative")
    private double advance;

    @Min(value = 0, message = "Remaining cost cannot be negative")
    private double remainingCost;

    @Size(max = 10, message = "Currency must not exceed 10 characters")
    private String currency;

    private LocalDate paymentDeadlineDate;

    @NotNull(message = "Provider ID is required")
    private Long providerId;
}
