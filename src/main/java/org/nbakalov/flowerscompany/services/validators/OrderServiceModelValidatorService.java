package org.nbakalov.flowerscompany.services.validators;

import org.nbakalov.flowerscompany.services.models.OrderServiceModel;

public interface OrderServiceModelValidatorService {

  boolean isValid(OrderServiceModel model);
}
