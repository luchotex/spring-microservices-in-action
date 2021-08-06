package com.optimagrowth.license.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

  public static final String CORRELATION_ID = "tmx-correlation-id";
  public static final String AUTH_TOKEN = "tmx-auth-token";
  public static final String USER_ID = "tmx-user-id";
  public static final String ORGANIZATION_ID = "tmx-organization-id";

  @Getter @Setter private String correlationId = new String();
  @Getter @Setter private String authToken = new String();
  @Getter @Setter private String userId = new String();
  @Getter @Setter private String organizationId = new String();
}
