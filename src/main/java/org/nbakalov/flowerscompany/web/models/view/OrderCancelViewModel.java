package org.nbakalov.flowerscompany.web.models.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nbakalov.flowerscompany.data.models.entities.Variety;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class OrderCancelViewModel {

  private String id;
  private String customer;
  private Variety variety;
  private Integer quantity;
  private Integer bunchesPerTray;
  private LocalDate orderDate;

}
