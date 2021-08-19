package com.optimagrowth.gatewayserver.filters;

import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class FilterUtil {

  public static final String CORRELATION_ID = "tmx-correlation-id";
  public static final String AUTH_TOKEN = "Authorization";

  public String getCorrelationId(HttpHeaders requestHeaders) {
    if (!Objects.isNull(requestHeaders.get(CORRELATION_ID))) {
      List<String> header = requestHeaders.get(CORRELATION_ID);
      return header.stream().findFirst().get();
    } else {
      return null;
    }
  }

  public String getAuthToken(HttpHeaders requestHeaders) {
    if (!Objects.isNull(requestHeaders.get(AUTH_TOKEN))) {
      List<String> header = requestHeaders.get(AUTH_TOKEN);
      return header.stream().findFirst().get();
    } else {
      return null;
    }
  }

  public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String value) {
    return setRequestHeader(exchange, CORRELATION_ID, value);
  }

  public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
    return exchange
        .mutate()
        .request(exchange.getRequest().mutate().header(name, value).build())
        .build();
  }
}
