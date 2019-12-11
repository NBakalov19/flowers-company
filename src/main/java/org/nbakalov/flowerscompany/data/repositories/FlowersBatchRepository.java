package org.nbakalov.flowerscompany.data.repositories;

import org.nbakalov.flowerscompany.data.models.entities.FlowersBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlowersBatchRepository extends JpaRepository<FlowersBatch, String> {

  List<FlowersBatch> findAllByDatePicked(LocalDate date);

}
