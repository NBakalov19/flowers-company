package org.nbakalov.flowerscompany.validations.user;

import lombok.AllArgsConstructor;
import org.nbakalov.flowerscompany.data.models.models.users.UserCreateModel;
import org.nbakalov.flowerscompany.data.repositories.UserRepository;
import org.nbakalov.flowerscompany.validations.annotation.Validation;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.nbakalov.flowerscompany.constants.UserConstants.*;

@Validation
@AllArgsConstructor
public class UserCreateValidation implements Validator {

  private final UserRepository userRepository;

  @Override
  public boolean supports(Class<?> aClass) {
    return UserCreateModel.class.equals(aClass);
  }

  @Override
  public void validate(Object obj, Errors errors) {
    UserCreateModel createModel = (UserCreateModel) obj;

    if (createModel.getUsername() == null ||
            createModel.getUsername().isEmpty()) {
      errors.rejectValue(
              "username", USERNAME_CAN_NOT_BE_EMPTY, USERNAME_CAN_NOT_BE_EMPTY);
    }

    if (createModel.getUsername().length() < USERNAME_MIN_LENGTH ||
            createModel.getUsername().length() > USERNAME_MAX_LENGTH) {
      errors.rejectValue(
              "username", USERNAME_LENGTH_NOT_CORRECT, USERNAME_LENGTH_NOT_CORRECT);
    }

    if (createModel.getPassword() == null
            || createModel.getPassword().isEmpty()) {
      errors.rejectValue(
              "password", PASSWORD_CAN_NOT_BE_EMPTY, PASSWORD_CAN_NOT_BE_EMPTY);
    }

    if (createModel.getPassword().length() < PASSWORD_MIN_LENGTH ||
            createModel.getPassword().length() > PASSWORD_MAX_LENGTH) {
      errors.rejectValue(
              "password", PASSWORD_LENGTH_NOT_CORRECT, PASSWORD_LENGTH_NOT_CORRECT);
    }

    if (!createModel.getPassword().equals(createModel.getConfirmPassword())) {
      errors.rejectValue(
              "password", PASSWORDS_NOT_MATCH, PASSWORDS_NOT_MATCH);
    }

    if (errors.hasErrors()) {
      return;
    }

    if (userRepository.findByUsername(createModel.getUsername()).isPresent()) {
      errors.rejectValue(
              "username",
              String.format(USERNAME_ALLREADY_EXIST, createModel.getUsername()),
              String.format(USERNAME_ALLREADY_EXIST, createModel.getUsername()));
    }

    if (userRepository.findByEmail(createModel.getEmail()).isPresent()) {
      errors.rejectValue(
              "email",
              String.format(USER_WITH_EMAIL_ALLREADY_EXIST, createModel.getEmail()),
              String.format(USER_WITH_EMAIL_ALLREADY_EXIST, createModel.getEmail()));
    }
  }
}
