package org.nbakalov.flowerscompany.web.models.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class OrderViewModel {

  private String id;
  private String customer;
  private String variety;
  private Integer quantity;
  private Integer bunchesPerTray;
  private LocalDate orderDate;
  private LocalDate finishedOn;
  private String status;
}
