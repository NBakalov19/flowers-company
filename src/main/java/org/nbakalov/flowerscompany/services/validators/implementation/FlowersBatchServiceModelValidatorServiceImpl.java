package org.nbakalov.flowerscompany.services.validators.implementation;

import org.nbakalov.flowerscompany.data.models.enums.Variety;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.validators.FlowersBatchServiceModelValidatorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlowersBatchServiceModelValidatorServiceImpl implements FlowersBatchServiceModelValidatorService {

  private static final List<Integer> VALID_BUNCHES_PER_TRAY_COUNT = List.of(10, 15, 16, 17, 18, 20, 25);
  private static final List<Variety> VALID_VARIETIES = Variety.stream().collect(Collectors.toList());

  @Override
  public boolean isValid(FlowersBatchServiceModel model) {
    return isTeamSupervisiorNameValid(model.getTeamSupervisor())
            && isFieldNameValid(model.getFieldName())
            && isVarietyValid(model.getVariety())
            && areBatchTraysValid(model.getTrays())
            && areBunchesPerTrayValid(model.getBunchesPerTray());
  }

  private boolean isTeamSupervisiorNameValid(String name) {
    return name.length() >= 3 && name.length() <= 20;
  }

  private boolean isFieldNameValid(String name) {
    return name.length() >= 3 && name.length() <= 20;
  }

  private boolean isVarietyValid(Variety variety) {
    return VALID_VARIETIES.contains(variety);
  }

  private boolean areBatchTraysValid(Integer trays) {
    return trays >= 1 && trays <= 1000;
  }

  private boolean areBunchesPerTrayValid(Integer bunchesPerTray) {

    return VALID_BUNCHES_PER_TRAY_COUNT.contains(bunchesPerTray);
  }
}
