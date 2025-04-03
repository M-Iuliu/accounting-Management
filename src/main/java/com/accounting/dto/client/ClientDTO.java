package com.accounting.dto.client;

import com.accounting.dto.offer.OfferDTO;
import com.accounting.entity.Offer;
import com.accounting.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientDTO {
    private Long clientId;

    private String name;
    private String title;
    private String surname;
    private String telephone;
    private String email;
    private List<Offer> offerList;
    private List<Reservation> reservationList;

}
