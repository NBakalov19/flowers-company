package org.nbakalov.flowerscompany.validations.flowers;

import lombok.NoArgsConstructor;
import org.nbakalov.flowerscompany.data.models.models.flowers.FlowersBatchCreateModel;
import org.nbakalov.flowerscompany.validations.annotation.Validation;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.nbakalov.flowerscompany.constants.FlowersBatchConstants.*;
import static org.nbakalov.flowerscompany.constants.GlobalConstants.VALID_VARIETIES;

@Validation
@NoArgsConstructor
public class FlowersBatchCreateValidation implements Validator {

  @Override
  public boolean supports(Class<?> aClass) {
    return FlowersBatchCreateModel.class.equals(aClass);
  }

  @Override
  public void validate(Object obj, Errors errors) {

    FlowersBatchCreateModel createModel = (FlowersBatchCreateModel) obj;

    if (createModel.getTeamSupervisor() == null
            || createModel.getTeamSupervisor().isEmpty()) {
      errors.rejectValue("teamSupervisor",
              TEAM_SUPERVISOR_NAME_CAN_NOT_BE_EMPTY,
              TEAM_SUPERVISOR_NAME_CAN_NOT_BE_EMPTY);
    }

    if (createModel.getTeamSupervisor().length() < TEAM_SUPERVISOR_NAME_MIN_LENGTH ||
            createModel.getTeamSupervisor().length() > TEAM_SUPERVISOR_NAME_MAX_LENGTH) {
      errors.rejectValue(
              "teamSupervisor", TEAM_SUPERVISOR_NAME_NOT_CORRECT, TEAM_SUPERVISOR_NAME_NOT_CORRECT);
    }

    if (createModel.getFieldName() == null
            || createModel.getFieldName().isEmpty()) {
      errors.rejectValue("fieldName",
              FIELD_NAME_CAN_NOT_BE_EMPTY,
              FIELD_NAME_CAN_NOT_BE_EMPTY);
    }

    if (createModel.getFieldName().length() < FIELD_NAME_MIN_LENGTH ||
            createModel.getFieldName().length() > FIELD_NAME_MAX_LENGTH) {
      errors.rejectValue(
              "fieldName", FIELD_NAME_NOT_CORRECT, FIELD_NAME_NOT_CORRECT);
    }

    if (createModel.getVariety() == null) {
      errors.rejectValue(
              "variety", VARIETY_CAN_NOT_BE_NULL, VARIETY_CAN_NOT_BE_NULL);
    }

    if (!VALID_VARIETIES.contains(createModel.getVariety())) {
      errors.rejectValue(
              "variety", VARIETY_NOT_CORRECT, VARIETY_NOT_CORRECT);
    }

    if (createModel.getTrays() < TRAYS_MIN_QUANTITY
            || createModel.getTrays() > TRAYS_MAX_QUANTITY) {
      errors.rejectValue(
              "trays", TRAYS_QUANTITY_NOT_CORRECT, TRAYS_QUANTITY_NOT_CORRECT);
    }

    if (createModel.getWarehouse() == null ||
            createModel.getWarehouse().isEmpty()) {
      errors.rejectValue(
              "warehouse", WAREHOUSE_CAN_NOT_BE_NULL, WAREHOUSE_CAN_NOT_BE_NULL);
    }
  }
}
