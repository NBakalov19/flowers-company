package org.nbakalov.flowerscompany.services.validators.implementation;

import lombok.NoArgsConstructor;
import org.nbakalov.flowerscompany.data.models.enums.Variety;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.validators.FlowersBatchServiceModelValidatorService;
import org.springframework.stereotype.Service;

import static org.nbakalov.flowerscompany.constants.GlobalConstants.VALID_BUNCHES_PER_TRAY_COUNT;
import static org.nbakalov.flowerscompany.constants.GlobalConstants.VALID_VARIETIES;

@Service
@NoArgsConstructor
public class FlowersBatchServiceModelValidatorServiceImpl implements FlowersBatchServiceModelValidatorService {

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
