package com.accounting.dto.reservation;

import com.accounting.dto.CommentDTO;
import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.provider.ProviderDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationDTO {
    private Long reservationId;
    private Long offerId;
    private ClientShortDTO client;
    private List<String> participants;
    private List<Long> childrenAge;

    private String bookingRef;
    private Date bookedDate;

    private LocalDate departureDate;
    private LocalDate returnDate;
    //    private int personsNumber;
    private int roomNo;
    private String destination;
    private String hotel;
    private String transportType;
    private double price;
    private double receipted;
    private double balance;
    private LocalDate balanceDueDate;
    private String currency;
    private ProviderDTO provider;
    private List<String> uploadedFiles;
    private List<CommentDTO> commentList;

}
