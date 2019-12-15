package org.nbakalov.flowerscompany.web.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class FaviconInterceptor extends HandlerInterceptorAdapter {

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response,
                         Object handler, ModelAndView modelAndView) {
    String link = "https://res.cloudinary.com/nikolaybakalov/image/upload/v1576420154/favicon-32x32_geidqg.png";

    if (modelAndView != null) {
      modelAndView.addObject("favicon", link);
    }
  }
}
