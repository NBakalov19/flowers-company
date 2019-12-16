package org.nbakalov.flowerscompany.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Incorrect password")
public class WrongPasswordException extends BaseException {
  public WrongPasswordException(String message) {
    super(message, HttpStatus.UNAUTHORIZED.value());
  }
}
