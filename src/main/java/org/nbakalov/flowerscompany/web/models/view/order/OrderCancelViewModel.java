package org.nbakalov.flowerscompany.web.models.view.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nbakalov.flowerscompany.data.models.enums.Variety;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class OrderCancelViewModel {

  private String id;
  private String customer;
  private Variety variety;
  private Integer quantity;
  private Integer bunchesPerTray;
  private LocalDateTime orderDate;

}
