package org.nbakalov.flowerscompany.web.models.view.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class OrderViewModel {

  private String id;
  private String customer;
  private String variety;
  private Integer quantity;
  private Integer bunchesPerTray;
  private LocalDateTime orderDate;
  private LocalDateTime finishedOn;
  private String status;
}
