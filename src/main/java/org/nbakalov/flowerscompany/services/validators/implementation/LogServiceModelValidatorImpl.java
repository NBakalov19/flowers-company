package org.nbakalov.flowerscompany.services.validators.implementation;

import lombok.NoArgsConstructor;
import org.nbakalov.flowerscompany.services.models.LogServiceModel;
import org.nbakalov.flowerscompany.services.validators.LogServiceModelValidatorService;
import org.springframework.stereotype.Service;

import static org.nbakalov.flowerscompany.constants.GlobalConstants.USERNAME_MAX_LENGTH;
import static org.nbakalov.flowerscompany.constants.GlobalConstants.USERNAME_MIN_LENGTH;
import static org.nbakalov.flowerscompany.constants.LogConstants.DESCRIPTION_MAX_LENGTH;
import static org.nbakalov.flowerscompany.constants.LogConstants.DESCRIPTION_MIN_LENGTH;

@Service
@NoArgsConstructor
public class LogServiceModelValidatorImpl implements LogServiceModelValidatorService {


  @Override
  public boolean isValid(LogServiceModel serviceModel) {
    return isValidUsername(serviceModel.getUsername())
            && isValidDescription(serviceModel.getDescription());
  }

  private boolean isValidDescription(String description) {
    return description.length() < DESCRIPTION_MIN_LENGTH ||
            description.length() > DESCRIPTION_MAX_LENGTH;
  }

  private boolean isValidUsername(String username) {
    return username.length() < USERNAME_MIN_LENGTH ||
            username.length() > USERNAME_MAX_LENGTH;
  }


}
