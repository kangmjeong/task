/* (C) 2024 AladdinSystem License */
package aladdinsys.api.task.common;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/docs/**")
        .addResourceLocations("classpath:/static/docs/")
        .setCachePeriod(20)
        .resourceChain(true)
        .addResolver(new PathResourceResolver());
  }
}
