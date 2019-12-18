package org.nbakalov.flowerscompany.validations.orders;

import lombok.NoArgsConstructor;
import org.nbakalov.flowerscompany.data.models.models.orders.OrderCreateModel;
import org.nbakalov.flowerscompany.validations.annotation.Validation;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.nbakalov.flowerscompany.constants.OrderConstants.ORDER_MIN_QUANTITY;
import static org.nbakalov.flowerscompany.constants.OrderConstants.ORDER_QUANTITY_CAN_NOT_BE_ZERO;

@Validation
@NoArgsConstructor
public class OrderCreateValidation implements Validator {

  @Override
  public boolean supports(Class<?> aClass) {
    return OrderCreateModel.class.equals(aClass);
  }

  @Override
  public void validate(Object obj, Errors errors) {

    OrderCreateModel createModel = (OrderCreateModel) obj;

    if (createModel.getQuantity() < ORDER_MIN_QUANTITY) {
      errors.rejectValue(
              "quantity", ORDER_QUANTITY_CAN_NOT_BE_ZERO, ORDER_QUANTITY_CAN_NOT_BE_ZERO);
    }
  }
}
