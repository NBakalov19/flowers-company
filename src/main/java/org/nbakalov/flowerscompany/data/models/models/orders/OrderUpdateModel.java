package org.nbakalov.flowerscompany.data.models.models.orders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nbakalov.flowerscompany.data.models.entities.Variety;

@NoArgsConstructor
@Getter
@Setter
public class OrderUpdateModel {

  private Variety variety;
  private Integer quantity;
  private Integer bunchesPerTray;
}
