package org.nbakalov.flowerscompany.services.validations;

import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;

public interface FlowersBatchServiceModelValidatorService {

  boolean isValid(FlowersBatchServiceModel model);
}
