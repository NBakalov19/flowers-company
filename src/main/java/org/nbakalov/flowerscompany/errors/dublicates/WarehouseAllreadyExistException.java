package org.nbakalov.flowerscompany.errors.dublicates;

import org.nbakalov.flowerscompany.errors.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Duplicate warehouse")
public class WarehouseAllreadyExistException extends BaseException {

  public WarehouseAllreadyExistException(String message) {
    super(message, HttpStatus.CONFLICT.value());
  }
}
