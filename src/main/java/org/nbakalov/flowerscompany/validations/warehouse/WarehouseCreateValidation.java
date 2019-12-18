package org.nbakalov.flowerscompany.validations.warehouse;

import lombok.AllArgsConstructor;
import org.nbakalov.flowerscompany.data.models.models.warehouse.WarehouseCreateModel;
import org.nbakalov.flowerscompany.data.repositories.WarehouseRepository;
import org.nbakalov.flowerscompany.validations.annotation.Validation;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.nbakalov.flowerscompany.constants.WarehouseConstants.*;

@Validation
@AllArgsConstructor
public class WarehouseCreateValidation implements Validator {

  private final WarehouseRepository warehouseRepository;

  @Override
  public boolean supports(Class<?> aClass) {
    return WarehouseCreateModel.class.equals(aClass);
  }

  @Override
  public void validate(Object obj, Errors errors) {
    WarehouseCreateModel createModel = (WarehouseCreateModel) obj;

    if (createModel.getName() == null || createModel.getName().isEmpty()) {
      errors.rejectValue(
              "name", WAREHOUSE_CAN_NOT_BE_EMPTY, WAREHOUSE_CAN_NOT_BE_EMPTY);
    }

    if (createModel.getName().length() < WAREHOUSE_NAME_MIN_LENGTH
            || createModel.getName().length() > WAREHOUSE_NAME_MAX_LENGTH) {
      errors.rejectValue(
              "name",
              WAREHOUSE_NAME_LENGTH_NOT_CORRECT, WAREHOUSE_NAME_LENGTH_NOT_CORRECT);
    }

    if (createModel.getMaxCapacity() < WAREHOUSE_MAX_CAPACITY_MIN_VALUE
            || createModel.getMaxCapacity() > WAREHOUSE_MAX_CAPACITY_MAX_VALUE) {
      errors.rejectValue(
              "maxCapacity",
              WAREHOUSE_MAX_CAPACITY_NOT_CORRECT, WAREHOUSE_MAX_CAPACITY_NOT_CORRECT);
    }

    if (errors.hasErrors()) {
      return;
    }

    if (warehouseRepository.findByName(createModel.getName()).isPresent()) {
      errors.rejectValue(
              "name", WAREHOUSE_ALLREADY_EXIST, WAREHOUSE_ALLREADY_EXIST);
    }
  }
}
