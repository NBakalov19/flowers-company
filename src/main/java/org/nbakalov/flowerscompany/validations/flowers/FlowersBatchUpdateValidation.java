package org.nbakalov.flowerscompany.validations.flowers;

import org.nbakalov.flowerscompany.data.models.models.flowers.FlowersBatchUpdateModel;
import org.nbakalov.flowerscompany.validations.annotation.Validation;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.nbakalov.flowerscompany.constants.FlowersBatchConstants.*;
import static org.nbakalov.flowerscompany.constants.GlobalConstants.VALID_VARIETIES;

@Validation
public class FlowersBatchUpdateValidation implements Validator {

  @Override
  public boolean supports(Class<?> aClass) {
    return FlowersBatchUpdateModel.class.equals(aClass);
  }

  @Override
  public void validate(Object obj, Errors errors) {
    FlowersBatchUpdateModel updateModel = (FlowersBatchUpdateModel) obj;

    if (updateModel.getTeamSupervisor() == null
            || updateModel.getTeamSupervisor().isEmpty()) {
      errors.rejectValue("teamSupervisor",
              TEAM_SUPERVISOR_NAME_CAN_NOT_BE_EMPTY, FIELD_NAME_CAN_NOT_BE_EMPTY);
    }

    if (updateModel.getTeamSupervisor().length() < TEAM_SUPERVISOR_NAME_MIN_LENGTH ||
            updateModel.getTeamSupervisor().length() > TEAM_SUPERVISOR_NAME_MAX_LENGTH) {
      errors.rejectValue("teamSupervisor",
              TEAM_SUPERVISOR_NAME_NOT_CORRECT, TEAM_SUPERVISOR_NAME_NOT_CORRECT);
    }

    if (updateModel.getFieldName() == null
            || updateModel.getFieldName().isEmpty()) {
      errors.rejectValue("fieldName",
              FIELD_NAME_CAN_NOT_BE_EMPTY, FIELD_NAME_CAN_NOT_BE_EMPTY);
    }

    if (updateModel.getFieldName().length() < FIELD_NAME_MIN_LENGTH ||
            updateModel.getFieldName().length() > FIELD_NAME_MAX_LENGTH) {
      errors.rejectValue("fieldName",
              FIELD_NAME_NOT_CORRECT, FIELD_NAME_NOT_CORRECT);
    }

    if (updateModel.getVariety() == null) {
      errors.rejectValue(
              "variety", VARIETY_CAN_NOT_BE_NULL, VARIETY_CAN_NOT_BE_NULL);
    }

    if (!VALID_VARIETIES.contains(updateModel.getVariety())) {
      errors.rejectValue(
              "variety", VARIETY_NOT_CORRECT, VARIETY_NOT_CORRECT);
    }

    if (updateModel.getTrays() < TRAYS_MIN_QUANTITY
            || updateModel.getTrays() > TRAYS_MAX_QUANTITY) {
      errors.rejectValue(
              "trays", TRAYS_QUANTITY_NOT_CORRECT, TRAYS_QUANTITY_NOT_CORRECT);
    }
  }
}
