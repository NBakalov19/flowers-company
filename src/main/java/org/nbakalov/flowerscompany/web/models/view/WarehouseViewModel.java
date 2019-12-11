package org.nbakalov.flowerscompany.web.models.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class WarehouseViewModel {

  private String name;
  private Integer maxCapacity;
  private Integer currCapacity;
  private Double temperature;
  private Set<FlowersBatchViewModel> batches;
}
