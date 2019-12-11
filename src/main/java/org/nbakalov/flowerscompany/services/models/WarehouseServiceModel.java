package org.nbakalov.flowerscompany.services.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class WarehouseServiceModel extends BaseServiceModel {

  private String name;
  private Integer maxCapacity;
  private Integer currCapacity;
  private Double temperature;
  private Set<FlowersBatchServiceModel> batches;
}
