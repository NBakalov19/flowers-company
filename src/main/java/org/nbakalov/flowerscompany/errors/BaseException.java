package org.nbakalov.flowerscompany.errors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends RuntimeException {

  private int status;

  public BaseException(String message, int status) {
    super(message);
    this.status = status;
  }
}
