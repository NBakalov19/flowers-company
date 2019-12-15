package org.nbakalov.flowerscompany.web.models.view.warehouese;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class WarehouseDetailsViewModel {

  private String id;
  private String name;
  private Integer maxCapacity;
  private Integer currCapacity;
  private Double temperature;
  private Set<FlowersBatchServiceModel> batches;
}
