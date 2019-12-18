package org.nbakalov.flowerscompany.validations.user;

import lombok.AllArgsConstructor;
import org.nbakalov.flowerscompany.data.models.entities.User;
import org.nbakalov.flowerscompany.data.models.models.users.UserUpdateModel;
import org.nbakalov.flowerscompany.data.repositories.UserRepository;
import org.nbakalov.flowerscompany.validations.annotation.Validation;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

import static org.nbakalov.flowerscompany.constants.UserConstants.PASSWORDS_NOT_MATCH;
import static org.nbakalov.flowerscompany.constants.UserConstants.USER_WITH_EMAIL_ALLREADY_EXIST;

@Validation
@AllArgsConstructor
public class UserUpdateValidation implements Validator {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public boolean supports(Class<?> aClass) {
    return UserUpdateModel.class.equals(aClass);
  }

  @Override
  public void validate(Object obj, Errors errors) {

    UserUpdateModel updateModel = (UserUpdateModel) obj;

    User user = userRepository.findByUsername(updateModel.getUsername()).orElse(null);

    if (!bCryptPasswordEncoder.matches(updateModel.getOldPassword(), Objects.requireNonNull(user).getPassword())
            || (updateModel.getPassword() != null
            && !updateModel.getPassword().equals(updateModel.getConfirmPassword()))) {
      errors.rejectValue(
              "oldPassword", PASSWORDS_NOT_MATCH, PASSWORDS_NOT_MATCH);
      errors.rejectValue(
              "password", PASSWORDS_NOT_MATCH, PASSWORDS_NOT_MATCH);
    }

    if (errors.hasErrors()) {
      return;
    }

    if (!user.getEmail().equals(updateModel.getEmail())
            && userRepository.findByEmail(updateModel.getEmail()).isPresent()) {
      errors.rejectValue(
              "email",
              String.format(USER_WITH_EMAIL_ALLREADY_EXIST, updateModel.getEmail()),
              String.format(USER_WITH_EMAIL_ALLREADY_EXIST, updateModel.getEmail()));
    }
  }
}
