package org.nbakalov.flowerscompany.web.models.view.warehouese;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AllWarehousesViewModel {

  private String id;
  private String name;
  private Double temperature;
  private Integer currCapacity;
  private Integer maxCapacity;
}
