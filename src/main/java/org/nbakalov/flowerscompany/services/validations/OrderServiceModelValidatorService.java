package org.nbakalov.flowerscompany.services.validations;

import org.nbakalov.flowerscompany.services.models.OrderServiceModel;

public interface OrderServiceModelValidatorService {

  boolean isValid(OrderServiceModel model);
}
