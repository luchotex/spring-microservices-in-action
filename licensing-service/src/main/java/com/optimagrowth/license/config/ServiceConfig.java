package com.optimagrowth.license.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceConfig {
  @Value("${example.property}")
  @Getter
  private String exampleProperty;
}
