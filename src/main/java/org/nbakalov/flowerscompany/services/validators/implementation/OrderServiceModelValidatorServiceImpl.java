package org.nbakalov.flowerscompany.services.validators.implementation;

import lombok.NoArgsConstructor;
import org.nbakalov.flowerscompany.data.models.enums.Variety;
import org.nbakalov.flowerscompany.services.models.OrderServiceModel;
import org.nbakalov.flowerscompany.services.validators.OrderServiceModelValidatorService;
import org.springframework.stereotype.Service;

import static org.nbakalov.flowerscompany.constants.GlobalConstants.VALID_BUNCHES_PER_TRAY_COUNT;
import static org.nbakalov.flowerscompany.constants.GlobalConstants.VALID_VARIETIES;

@Service
@NoArgsConstructor
public class OrderServiceModelValidatorServiceImpl implements OrderServiceModelValidatorService {

  @Override
  public boolean isValid(OrderServiceModel model) {
    return isVarietyValid(model.getVariety())
            && areQuantityValid(model.getQuantity())
            && areBunchesPerTrayValid(model.getBunchesPerTray());
  }

  private boolean isVarietyValid(Variety variety) {
    return VALID_VARIETIES.contains(variety) || variety == null;
  }

  private boolean areQuantityValid(Integer trays) {
    return trays >= 1 && trays <= 10000;
  }

  private boolean areBunchesPerTrayValid(Integer bunchesPerTray) {

    return VALID_BUNCHES_PER_TRAY_COUNT.contains(bunchesPerTray);
  }

}
