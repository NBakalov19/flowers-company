package org.nbakalov.flowerscompany.services.validations.implementation;

import org.nbakalov.flowerscompany.data.models.entities.Status;
import org.nbakalov.flowerscompany.data.models.entities.Variety;
import org.nbakalov.flowerscompany.services.models.OrderServiceModel;
import org.nbakalov.flowerscompany.services.validations.OrderServiceModelValidatorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceModelValidatorServiceImpl implements OrderServiceModelValidatorService {

  private static final List<Integer> VALID_BUNCHES_PER_TRAY_COUNT = List.of(10, 15, 16, 17, 18, 20, 25);
  private static final List<Variety> VALID_VARIETIES = Variety.stream().collect(Collectors.toList());
  private static final List<Status> VALID_STATUS = Status.stream().collect(Collectors.toList());

  @Override
  public boolean isValid(OrderServiceModel model) {
    return isVarietyValid(model.getVariety())
            && areQuantityValid(model.getQuantity())
            && areBunchesPerTrayValid(model.getBunchesPerTray())
            && isStatusValid(model.getStatus());
  }

  private boolean isVarietyValid(Variety variety) {
    return VALID_VARIETIES.contains(variety) || variety == null;
  }

  private boolean areQuantityValid(Integer trays) {
    return trays >= 1 && trays <= 1000;
  }

  private boolean areBunchesPerTrayValid(Integer bunchesPerTray) {

    return VALID_BUNCHES_PER_TRAY_COUNT.contains(bunchesPerTray);
  }

  private boolean isStatusValid(Status status) {
    return VALID_STATUS.contains(status);
  }
}
