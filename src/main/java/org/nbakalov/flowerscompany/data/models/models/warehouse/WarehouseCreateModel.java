package org.nbakalov.flowerscompany.data.models.models.warehouse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class WarehouseCreateModel {

  private String name;
  private Integer maxCapacity;
}
