package org.nbakalov.flowerscompany.errors.illegalservicemodels;

import org.nbakalov.flowerscompany.errors.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Illegal flowers batch service model")
public class IllegalLogServiceModelException extends BaseException {

  public IllegalLogServiceModelException(String message) {
    super(message, HttpStatus.NOT_ACCEPTABLE.value());
  }
}
