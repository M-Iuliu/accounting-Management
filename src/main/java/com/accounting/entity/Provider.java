package com.accounting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long providerId;

    private String telephone;
    private String providerName;
    private String email;
    private String webLink;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "reservation_provider",
            joinColumns = @JoinColumn(name = "provider_id"),
            inverseJoinColumns = @JoinColumn(name = "reservation_id"))
    private List<Reservation> reservationList;

    private Boolean isDeleted;
    private Date deletionDate; //TODO: to add Auditable to all classes

}
