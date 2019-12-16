package org.nbakalov.flowerscompany.services.validations.implementation;

import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.nbakalov.flowerscompany.services.validations.UserServiceModelValidatorService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceModelValidatorServiceImpl implements UserServiceModelValidatorService {

  @Override
  public boolean isValid(UserServiceModel model) {
    return isUsernameValid(model.getUsername())
            && isPasswordValid(model.getPassword())
            && isEmailValid(model.getEmail())
            && isProfilePictureValid(model.getProfilePictureUrl());
  }

  boolean isUsernameValid(String username) {
    return username.length() >= 3 && username.length() <= 20;
  }

  boolean isPasswordValid(String password) {
    return password.length() >= 6 && password.length() <= 15;
  }

  boolean isEmailValid(String email) {
    return !email.isEmpty();
  }

  boolean isProfilePictureValid(String picture) {
    return !picture.isEmpty();
  }
}
