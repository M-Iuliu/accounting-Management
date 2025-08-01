package com.accounting.repository;

import com.accounting.entity.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    @Query("SELECT p FROM Provider p " +
            "WHERE LOWER(p.providerName) LIKE LOWER(CONCAT('%', :input, '%')) " +
            "OR p.telephone LIKE CONCAT('%', :input, '%') " )
    Page<Provider> findByFilter(@Param("input") String input, Pageable pageable);

}
