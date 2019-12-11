package org.nbakalov.flowerscompany.web.models.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nbakalov.flowerscompany.data.models.entities.Variety;

@NoArgsConstructor
@Getter
@Setter
public class FlowersBatchViewModel {

  private String id;
  private String teamSupervisor;
  private String fieldName;
  private Variety variety;
  private Integer trays;
  private Integer bunchesPerTray;
  private String warehouse;
}
