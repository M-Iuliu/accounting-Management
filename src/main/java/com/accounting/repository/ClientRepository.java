package com.accounting.repository;

import com.accounting.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("SELECT c FROM Client c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :input, '%')) " +
            "OR LOWER(c.surname) LIKE LOWER(CONCAT('%', :input, '%')) " +
            "OR c.telephone LIKE CONCAT('%', :input, '%')" +
            "AND c.isDeleted is null")
    Page<Client> findByFilter(@Param("input") String input, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE c.isDeleted is null")
    Page<Client> findAllActiveClients(Pageable pageable);

}
