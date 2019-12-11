package org.nbakalov.flowerscompany.services.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nbakalov.flowerscompany.data.models.entities.Status;
import org.nbakalov.flowerscompany.data.models.entities.Variety;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class OrderServiceModel extends BaseServiceModel {

  private UserServiceModel customer;
  private Variety variety;
  private Integer quantity;
  private Integer bunchesInTray;
  private LocalDate orderDate;
  private LocalDate finishedOn;
  private Status status;
}
