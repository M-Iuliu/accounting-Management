package com.accounting.repository;

import com.accounting.entity.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObservationsRepository extends JpaRepository<Observation, Long> {

}
