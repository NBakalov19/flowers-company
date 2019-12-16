package org.nbakalov.flowerscompany.services.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nbakalov.flowerscompany.data.models.entities.Status;
import org.nbakalov.flowerscompany.data.models.entities.Variety;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class OrderServiceModel extends BaseServiceModel {

  private UserServiceModel customer;
  private Variety variety;
  private Integer quantity;
  private Integer bunchesPerTray;
  private LocalDateTime orderDate;
  private LocalDateTime finishedOn;
  private Status status;
}
