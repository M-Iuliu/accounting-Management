package com.accounting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToOne
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    //    mappedBy = "reservation" must match the field name in ReservationParticipants.
//    CascadeType.ALL ensures participants are saved/deleted along with the reservation.
//    orphanRemoval = true removes participants if they're removed from the list.
    @JsonIgnore
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationParticipant> participants = new ArrayList<>(); //editable

    private String bookingRef;
    private Date bookedDate;

    private Date departureDate;//editable
    private Date returnDate;//editable

    private String destination;//editable
    private String hotel;//editable
    private int roomNo;//editable
    private String transport; //editable TODO: enum ?//editable

    private double price;//editable
    private double receipted;//editable
    private double balance;//editable
    private Date paymentDueDate;//editable
    private String currency;//editable

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;//editable

    @JsonIgnore
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationFile> uploadedFiles;

    private Boolean isDeleted;
    private Date deletionDate; //TODO: to add Auditable to all classes
}
