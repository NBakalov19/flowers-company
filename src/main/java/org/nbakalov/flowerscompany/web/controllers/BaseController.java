package org.nbakalov.flowerscompany.web.controllers;

import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.web.servlet.ModelAndView;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseController {

  protected ModelAndView view(String viewName, ModelAndView modelAndView) {
    modelAndView.setViewName(viewName);

    return modelAndView;
  }

  protected ModelAndView view(String viewName) {
    return this.view(viewName, new ModelAndView());
  }

  protected ModelAndView redirect(String url) {
    return this.view("redirect:" + url);
  }
}
