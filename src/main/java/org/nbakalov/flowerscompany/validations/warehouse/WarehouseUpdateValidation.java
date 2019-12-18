package org.nbakalov.flowerscompany.validations.warehouse;

import lombok.AllArgsConstructor;
import org.nbakalov.flowerscompany.data.models.entities.Warehouse;
import org.nbakalov.flowerscompany.data.models.models.warehouse.WarehouseUpdateModel;
import org.nbakalov.flowerscompany.data.repositories.WarehouseRepository;
import org.nbakalov.flowerscompany.validations.annotation.Validation;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

import static org.nbakalov.flowerscompany.constants.WarehouseConstants.*;

@Validation
@AllArgsConstructor
public class WarehouseUpdateValidation implements Validator {

  private final WarehouseRepository warehouseRepository;

  @Override
  public boolean supports(Class<?> aClass) {
    return WarehouseUpdateModel.class.equals(aClass);
  }

  @Override
  public void validate(Object obj, Errors errors) {

    WarehouseUpdateModel updateModel = (WarehouseUpdateModel) obj;

    Warehouse warehouse = warehouseRepository.findById(updateModel.getId()).orElse(null);

    if (updateModel.getName() == null || updateModel.getName().isEmpty()) {
      errors.rejectValue(
              "name", WAREHOUSE_CAN_NOT_BE_EMPTY, WAREHOUSE_CAN_NOT_BE_EMPTY);
    }

    if (updateModel.getName().length() < WAREHOUSE_NAME_MIN_LENGTH
            || updateModel.getName().length() > WAREHOUSE_NAME_MAX_LENGTH) {
      errors.rejectValue(
              "name",
              WAREHOUSE_NAME_LENGTH_NOT_CORRECT, WAREHOUSE_NAME_LENGTH_NOT_CORRECT);
    }

    if (updateModel.getMaxCapacity() < WAREHOUSE_MAX_CAPACITY_MIN_VALUE
            || updateModel.getMaxCapacity() > WAREHOUSE_MAX_CAPACITY_MAX_VALUE) {
      errors.rejectValue(
              "maxCapacity",
              WAREHOUSE_MAX_CAPACITY_NOT_CORRECT, WAREHOUSE_MAX_CAPACITY_NOT_CORRECT);
    }

    if (errors.hasErrors()) {
      return;
    }

    if (updateModel.getName().equals(Objects.requireNonNull(warehouse).getName())) {
      errors.rejectValue(
              "name", WAREHOUSE_ALLREADY_EXIST, WAREHOUSE_ALLREADY_EXIST);
    }
  }
}
