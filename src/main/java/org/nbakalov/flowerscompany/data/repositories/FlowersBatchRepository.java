package org.nbakalov.flowerscompany.data.repositories;

import org.nbakalov.flowerscompany.data.models.entities.FlowersBatch;
import org.nbakalov.flowerscompany.data.models.entities.Variety;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlowersBatchRepository extends JpaRepository<FlowersBatch, String> {

  List<FlowersBatch> findAllByDatePicked(LocalDate date);

  List<FlowersBatch> findAllByBunchesPerTrayOrderByTrays(Integer bunchesPerTray);

  List<FlowersBatch> findAllByVarietyAndBunchesPerTrayOrderByTraysDesc(Variety variety, Integer bunchesPerTray);

  @Modifying
  @Query("delete from FlowersBatch as f where f.id = :id")
  void deleteById(@Param("id") String id);
}
