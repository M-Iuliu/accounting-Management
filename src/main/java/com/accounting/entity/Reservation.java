package com.accounting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"client", "offer", "provider", "participants", "uploadedFiles"})
@EqualsAndHashCode(callSuper = false)
public class Reservation extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;

    //    mappedBy = "reservation" must match the field name in ReservationParticipants.
//    CascadeType.ALL ensures participants are saved/deleted along with the reservation.
//    orphanRemoval = true removes participants if they're removed from the list.
    @JsonIgnore
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationParticipant> participants = new ArrayList<>();

    @Column(name = "booking_ref", length = 255)
    private String bookingRef;

    @Column(name = "booked_date")
    private LocalDate bookedDate;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(length = 255)
    private String destination;

    @Column(length = 255)
    private String hotel;

    @Column(name = "room_no")
    private int roomNo;

    @Column(length = 100)
    private String transport;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private double receipted;

    @Column(nullable = false)
    private double balance;

    @Column(name = "payment_due_date")
    private LocalDate paymentDueDate;

    @Column(length = 10)
    private String currency;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @JsonIgnore
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationFile> uploadedFiles;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deletion_date")
    private Date deletionDate;
}
