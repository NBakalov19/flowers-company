package org.nbakalov.flowerscompany.config;

import lombok.AllArgsConstructor;
import org.nbakalov.flowerscompany.web.interceptors.FaviconInterceptor;
import org.nbakalov.flowerscompany.web.interceptors.TitleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class ApplicationWebConfigurations implements WebMvcConfigurer {

  private final TitleInterceptor titleInterceptor;
  private final FaviconInterceptor faviconInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(titleInterceptor);
    registry.addInterceptor(faviconInterceptor);
  }
}
