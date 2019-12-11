package org.nbakalov.flowerscompany.web.models.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class MyOrdersApiModel {
  private String customer;
  private String variety;
  private Integer quantity;
  private Integer bunchesInTray;
  private LocalDate orderDate;
  private LocalDate finishedOn;
  private String status;
}
