package com.optimagrowth.license;

import com.optimagrowth.license.utils.UserContextInterceptor;
import java.util.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@SpringBootApplication
@RefreshScope
@EntityScan(basePackages = {"com.optimagrowth.license.model"}) // scan JPA entities manuall
@EnableDiscoveryClient
@EnableFeignClients
@EnableCircuitBreaker
public class LicensingServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(LicensingServiceApplication.class, args);
  }

  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver localeResolver = new SessionLocaleResolver();
    //        localeResolver.setDefaultLocale(Locale.US);
    return localeResolver;
  }

  @Bean
  public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setUseCodeAsDefaultMessage(true);
    messageSource.setBasenames("messages");

    return messageSource;
  }

  @Bean
  @LoadBalanced
  public RestTemplate getRestTemplate() {
    RestTemplate template = new RestTemplate();
    List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();

    if (Objects.isNull(interceptors)) {
      template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
    } else {
      interceptors.add(new UserContextInterceptor());
      template.setInterceptors(interceptors);
    }

    return template;
  }
}
