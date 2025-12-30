package com.accounting.repository;

import com.accounting.entity.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface OfferRepository extends JpaRepository<Offer, Long> {

    @Query("SELECT o FROM Offer o JOIN o.client c " +
            "WHERE o.isDeleted is null " +
            "AND (:showActive = false OR o.status = 'OFERTAT') " +
            "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :input, '%')) " +
            "OR LOWER(c.surname) LIKE LOWER(CONCAT('%', :input, '%')) " +
            "OR c.telephone LIKE CONCAT('%', :input, '%'))")
    Page<Offer> findByFilter(@Param("input") String input, Pageable pageable, @Param("showActive") boolean showActive);

    @Query("SELECT o FROM Offer o WHERE o.isDeleted is null AND (:showActive = false OR o.status = 'OFERTAT')")
    Page<Offer> findAllActiveOffers(Pageable pageable, @Param("showActive") boolean showActive);

    @Query("SELECT o FROM Offer o " +
            "WHERE o.deletionDate is NULL " +
            "AND o.status = 'OFERTAT' " +
            "AND o.offerDate = :targetDate")
    List<Offer> findOffersOlderThan(@Param("targetDate") LocalDate targetDate);
}
