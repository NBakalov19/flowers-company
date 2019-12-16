package org.nbakalov.flowerscompany.errors.notfound;

import org.nbakalov.flowerscompany.errors.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "fFlower batch not found.")
public class FlowersBatchNotFoundException extends BaseException {

  public FlowersBatchNotFoundException(String message) {
    super(message, HttpStatus.BAD_REQUEST.value());
  }
}
