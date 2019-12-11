package org.nbakalov.flowerscompany.web.controllers.view;

import lombok.NoArgsConstructor;
import org.nbakalov.flowerscompany.web.controllers.BaseController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@NoArgsConstructor
public class HomeController extends BaseController {

  @GetMapping("/index")
  @PreAuthorize("isAnonymous()")
  public ModelAndView index() {
    return view("index");
  }

  @GetMapping("/home")
  @PreAuthorize("isAuthenticated()")
  public ModelAndView home() {
    return view("home");
  }
}
