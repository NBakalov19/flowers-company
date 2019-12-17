package org.nbakalov.flowerscompany.web.models.view.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nbakalov.flowerscompany.data.models.enums.Variety;

@NoArgsConstructor
@Getter
@Setter
public class OrderEditViewModel {

  private String id;
  private Variety variety;
  private Integer quantity;
  private Integer bunchesPerTray;
}
