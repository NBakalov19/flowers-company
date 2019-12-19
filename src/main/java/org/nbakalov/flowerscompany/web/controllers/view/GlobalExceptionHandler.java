package org.nbakalov.flowerscompany.web.controllers.view;

import org.nbakalov.flowerscompany.errors.BaseException;
import org.nbakalov.flowerscompany.errors.WrongPasswordException;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.*;
import org.nbakalov.flowerscompany.errors.notfound.FlowersBatchNotFoundException;
import org.nbakalov.flowerscompany.errors.notfound.OrderNotFoundException;
import org.nbakalov.flowerscompany.errors.notfound.UserNotFoundException;
import org.nbakalov.flowerscompany.errors.notfound.WarehouseNotFoundException;
import org.nbakalov.flowerscompany.errors.unabled.NotPossibleToEmptyWarehouseException;
import org.nbakalov.flowerscompany.errors.unabled.NotPossibleToRegisterBatchException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import static org.nbakalov.flowerscompany.constants.ExceptionConstants.*;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Throwable.class)
  public ModelAndView handleGlobalException(Throwable ex) {

    ex.printStackTrace();
    Integer statusCode = Integer.parseInt(ex.getCause().getMessage());
    String message = ex.getMessage();

    return initModelAndView(statusCode, message);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ModelAndView handleUsernameNotFoundException(UsernameNotFoundException ex) {

    ex.printStackTrace();
    Integer statusCode = Integer.parseInt(ex.getCause().getMessage());
    String message = ex.getMessage();

    return initModelAndView(statusCode, message);
  }


  @ExceptionHandler({WrongPasswordException.class})
  public ModelAndView handleUnauthorizedExceptions(BaseException ex) {

    ex.printStackTrace();

    return initModelAndView(ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler({NotPossibleToRegisterBatchException.class,
          NotPossibleToEmptyWarehouseException.class,
          FlowersBatchNotFoundException.class,
          OrderNotFoundException.class,
          UserNotFoundException.class,
          WarehouseNotFoundException.class})
  public ModelAndView handleBadRequestExceptions(BaseException ex) {
    ex.printStackTrace();

    return initModelAndView(ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler({IllegalFlowersBatchServiceModelException.class,
          IllegalOrderServiceModelException.class,
          IllegalUserServiceModelException.class,
          IllegalWarehouseServiceModelException.class,
          IllegalLogServiceModelException.class})
  public ModelAndView handleNotAcceptableExceptions(BaseException ex) {
    ex.printStackTrace();

    return initModelAndView(ex.getStatus(), ex.getMessage());
  }

  private ModelAndView initModelAndView(Integer statusCode, String message) {

    ModelAndView modelAndView = new ModelAndView(EXCEPTION_VIEW_ROUTE);
    modelAndView.addObject(ERROR_PAGE_STATUS_CODE_ATTRIBUTE_NAME, statusCode);
    modelAndView.addObject(ERROR_PAGE_MESSAGE_ATTRIBUTE_NAME, message);

    return modelAndView;
  }
}