package org.nbakalov.flowerscompany.errors.illegalservicemodels;

import org.nbakalov.flowerscompany.errors.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Illegal user service model")
public class IllegalUserServiceModelException extends BaseException {

  public IllegalUserServiceModelException(String message) {
    super(message, HttpStatus.NOT_ACCEPTABLE.value());
  }
}
