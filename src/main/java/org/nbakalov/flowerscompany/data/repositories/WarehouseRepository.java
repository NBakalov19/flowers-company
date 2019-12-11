package org.nbakalov.flowerscompany.data.repositories;

import org.nbakalov.flowerscompany.data.models.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

  Optional<Warehouse> findByName(String name);

  Warehouse findFirstByOrderByCurrCapacityAsc();

  @Override
  @Query("select w from Warehouse as w order by w.name")
  List<Warehouse> findAll();
}
