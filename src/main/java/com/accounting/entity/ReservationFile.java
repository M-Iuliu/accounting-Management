package com.accounting.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a file uploaded for a reservation.
 * Tracks metadata about files stored in the file system.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "reservation")
@EqualsAndHashCode(callSuper = false)
public class ReservationFile extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "file_name", nullable = false, length = 500)
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
}
