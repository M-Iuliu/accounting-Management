package com.accounting.repository;

import com.accounting.entity.ReservationFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for ReservationFile entity.
 * Provides database access methods for file metadata associated with reservations.
 */
public interface ReservationFileRepository extends JpaRepository<ReservationFile, Long> {

    /**
     * Finds all files associated with a specific reservation.
     *
     * @param reservationId the reservation ID
     * @return list of reservation files
     */
    @Query("SELECT rf FROM ReservationFile rf WHERE rf.reservation.reservationId = :reservationId")
    List<ReservationFile> findByReservationId(@Param("reservationId") Long reservationId);

    /**
     * Finds a file by reservation ID and file name.
     *
     * @param reservationId the reservation ID
     * @param fileName the file name
     * @return the reservation file if found
     */
    @Query("SELECT rf FROM ReservationFile rf " +
           "WHERE rf.reservation.reservationId = :reservationId " +
           "AND rf.fileName = :fileName")
    ReservationFile findByReservationIdAndFileName(@Param("reservationId") Long reservationId,
                                                    @Param("fileName") String fileName);
}
