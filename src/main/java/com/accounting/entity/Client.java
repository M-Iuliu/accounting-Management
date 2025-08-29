package com.accounting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    private String title;
    private String name;
    private String surname;
    private String telephone;
    private String email;
    //TODO: localitate plecare
    private String obs;

    private Boolean isDeleted;
    private Date deletionDate; //TODO: to add Auditable to all classes

    @JsonIgnore
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Offer> offerList;

    @JsonIgnore
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservationList;

    public String getFullName(){
        return this.name + " " + this.surname;
    }

}
