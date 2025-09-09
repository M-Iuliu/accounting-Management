package com.accounting.repository;

import com.accounting.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r " +
            "JOIN r.client c " +
            "WHERE r.isDeleted is null " +
            "AND ((LOWER(c.name) LIKE LOWER(CONCAT('%', :input, '%')) " +
            "OR LOWER(c.surname) LIKE LOWER(CONCAT('%', :input, '%')) " +
            "OR c.telephone LIKE CONCAT('%', :input, '%')))")
    Page<Reservation> findByNameOrPhone(@Param("input") String input, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.isDeleted is null")
    Page<Reservation> findAllActiveReservations(Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.isDeleted is null")
    List<Reservation> findAllActiveReservations();

    @Query("SELECT r FROM Reservation r WHERE r.returnDate = :targetDate AND r.isDeleted is null")
    List<Reservation> findReservationsWithReturnDate(@Param("targetDate") Date targetDate);

    @Query("SELECT r FROM Reservation r WHERE r.departureDate = :targetDate AND r.isDeleted is null")
    List<Reservation> findReservationsWithDepartureDate(@Param("targetDate") Date targetDate);

    @Query("SELECT r FROM Reservation r WHERE r.paymentDueDate = :targetDate AND r.isDeleted is null")
    List<Reservation> findReservationsWithPaymentDueDate(@Param("targetDate") Date targetDate);

}
