package org.nbakalov.flowerscompany.services.validations;

import org.nbakalov.flowerscompany.services.models.UserServiceModel;

public interface UserServiceModelValidatorService {

  boolean isValid(UserServiceModel model);
}
