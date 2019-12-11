package org.nbakalov.flowerscompany.services.models;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.nbakalov.flowerscompany.data.models.entities.Variety;

@NoArgsConstructor
@Getter
@Setter
public class FlowersBatchServiceModel extends BaseServiceModel {

  private String teamSupervisor;
  private String fieldName;
  private Variety variety;
  private Integer trays;
  private Integer bunchesPerTray;
  private LocalDate datePicked;
  private WarehouseServiceModel warehouse;
}
