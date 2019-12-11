package org.nbakalov.flowerscompany.web.models.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class WarehouseUpdateViewModel {

  private String id;
  private String name;
  private Integer maxCapacity;
  private Integer currCapacity;
  private Double temperature;
}
