package org.nbakalov.flowerscompany.errors.unabled;

import org.nbakalov.flowerscompany.errors.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Is only one warehouse")
public class NotPossibleToEmptyWarehouseException extends BaseException {

  public NotPossibleToEmptyWarehouseException(String message) {
    super(message, HttpStatus.BAD_REQUEST.value());
  }
}
