package org.nbakalov.flowerscompany.data.models.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class WarehouseUpdateModel {

  private String name;
  private Integer maxCapacity;
  private Integer currCapacity;
  private Double temperature;
}
