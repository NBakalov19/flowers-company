package org.nbakalov.flowerscompany.services.validators.implementation;

import lombok.NoArgsConstructor;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.validators.WarehouseServiceModelValidatorService;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class WarehouseServiceModelValidatorServiceImpl implements WarehouseServiceModelValidatorService {

  @Override
  public boolean isValid(WarehouseServiceModel model) {
    return isWarehouseNameValid(model.getName())
            && isWarehouseMaxCapacityValid(model.getMaxCapacity());
  }

  boolean isWarehouseNameValid(String name) {
    return name.length() >= 4 && name.length() <= 20;
  }

  boolean isWarehouseMaxCapacityValid(Integer maxCapacity) {
    return maxCapacity >= 2500 && maxCapacity <= 50000;
  }
}
