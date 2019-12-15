package org.nbakalov.flowerscompany.web.models.view.flowerBatch;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class FlowerBatchDeleteViewModel {

  private String id;
  private String teamSupervisor;
  private String fieldName;
  private String variety;
  private Integer trays;
  private Integer bunchesPerTray;
  private String warehouse;
}
