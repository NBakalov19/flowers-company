package org.nbakalov.flowerscompany.web.models.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TodayFlowersBatchApiModel {

  private String id;
  private String variety;
  private String teamSupervisor;
  private String fieldName;
  private Integer trays;
  private Integer bunchesPerTray;
  private String warehouse;
}
