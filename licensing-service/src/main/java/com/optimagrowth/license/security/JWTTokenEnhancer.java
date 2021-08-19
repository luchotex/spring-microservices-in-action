package com.optimagrowth.license.security;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

public class JWTTokenEnhancer implements TokenEnhancer {

  @Override
  public OAuth2AccessToken enhance(
      OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
    Map<String, Object> additionalInfo = new HashMap<>();

    additionalInfo.put("authentication_name", oAuth2Authentication.getName());

    ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);

    return oAuth2AccessToken;
  }
}
