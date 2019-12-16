package org.nbakalov.flowerscompany.errors.unabled;

import org.nbakalov.flowerscompany.errors.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No room in warehouse.")
public class NotPossibleToRegisterBatchException extends BaseException {

  public NotPossibleToRegisterBatchException(String message) {
    super(message, HttpStatus.BAD_REQUEST.value());
  }
}
