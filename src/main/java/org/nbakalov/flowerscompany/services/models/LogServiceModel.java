package org.nbakalov.flowerscompany.services.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class LogServiceModel extends BaseServiceModel {

  private LocalDateTime createdOn;
  private String username;
  private String description;

}
