package org.nbakalov.flowerscompany.services.validations.implementation;

import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.validations.WarehouseServiceModelValidatorService;
import org.springframework.stereotype.Service;

@Service
public class WarehouseServiceModelValidatorServiceImpl implements WarehouseServiceModelValidatorService {

  @Override
  public boolean isValid(WarehouseServiceModel model) {
    return isWarehouseNameValid(model.getName())
            && isWarehouseMaxCapacityValid(model.getMaxCapacity());
  }

  boolean isWarehouseNameValid(String name) {
    return name.length() >= 3 && name.length() <= 20;
  }

  boolean isWarehouseMaxCapacityValid(Integer maxCapacity) {
    return maxCapacity >= 2500 && maxCapacity <= 50000;
  }
}
