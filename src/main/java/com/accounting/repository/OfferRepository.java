package com.accounting.repository;

import com.accounting.entity.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OfferRepository extends JpaRepository<Offer, Long> {

    @Query("SELECT o FROM Offer o JOIN o.client c " +
            "WHERE o.isDeleted is null " +
            "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :input, '%')) " +
            "OR LOWER(c.surname) LIKE LOWER(CONCAT('%', :input, '%')) " +
            "OR c.telephone LIKE CONCAT('%', :input, '%'))")
//            "OR FUNCTION('DATE', o.offerDate) = :offerDate" + //TODO: Ask EMi what he intended to do with offerDate
    Page<Offer> findByFilter(@Param("input") String input, Pageable pageable);

    @Query("SELECT o FROM Offer o WHERE o.isDeleted is null")
    Page<Offer> findAllActiveClients(Pageable pageable);
}
