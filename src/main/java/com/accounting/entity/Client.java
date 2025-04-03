package com.accounting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    private String title;
    private String name;
    private String surname;
    private String telephone;
    private String email;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "client_offer",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "offer_id"))
    private List<Offer> offerList;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "client_reservation",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "reservation_id"))
    private List<Reservation> reservationList;

}
