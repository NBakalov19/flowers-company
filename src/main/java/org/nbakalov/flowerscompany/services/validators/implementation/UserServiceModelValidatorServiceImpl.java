package org.nbakalov.flowerscompany.services.validators.implementation;

import lombok.NoArgsConstructor;
import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.nbakalov.flowerscompany.services.validators.UserServiceModelValidatorService;
import org.springframework.stereotype.Service;

import static org.nbakalov.flowerscompany.constants.GlobalConstants.USERNAME_MAX_LENGTH;
import static org.nbakalov.flowerscompany.constants.GlobalConstants.USERNAME_MIN_LENGTH;
import static org.nbakalov.flowerscompany.constants.UserConstants.*;

@Service
@NoArgsConstructor
public class UserServiceModelValidatorServiceImpl implements UserServiceModelValidatorService {

  @Override
  public boolean isValid(UserServiceModel model) {
    return isUsernameValid(model.getUsername())
            && isPasswordValid(model.getPassword())
            && isEmailValid(model.getEmail());
  }

  boolean isUsernameValid(String username) {
    return username.length() >= USERNAME_MIN_LENGTH
            && username.length() <= USERNAME_MAX_LENGTH;
  }

  boolean isPasswordValid(String password) {
    return password.length() >= PASSWORD_MIN_LENGTH
            && password.length() <= PASSWORD_MAX_LENGTH;

  }

  boolean isEmailValid(String email) {
    return !email.isEmpty();
  }
}
