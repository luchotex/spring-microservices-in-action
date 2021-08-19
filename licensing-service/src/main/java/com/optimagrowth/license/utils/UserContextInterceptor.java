package com.optimagrowth.license.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Slf4j
public class UserContextInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(
      HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution clientHttpRequestExecution)
      throws IOException {
    HttpHeaders headers = httpRequest.getHeaders();
    log.debug("Request Header {}", httpRequest.getHeaders().keySet());
    log.debug("Request body as bytes: {}", body);
    log.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));

    headers.add(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
    headers.add(UserContext.AUTH_TOKEN, UserContextHolder.getContext().getAuthToken());

    return clientHttpRequestExecution.execute(httpRequest, body);
  }
}
