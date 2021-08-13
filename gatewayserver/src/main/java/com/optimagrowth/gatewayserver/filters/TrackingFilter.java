package com.optimagrowth.gatewayserver.filters;

import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1)
@Component
@Slf4j
public class TrackingFilter implements GlobalFilter {

  private FilterUtil filterUtil;

  public TrackingFilter(FilterUtil filterUtil) {
    this.filterUtil = filterUtil;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    HttpHeaders header = exchange.getRequest().getHeaders();
    if (isCorrelationIdPresent(header)) {
      log.debug(
          "{} found in tracking filter: {}",
          FilterUtil.CORRELATION_ID,
          filterUtil.getCorrelationId(header));
    } else {
      String correlationId = generateCorrelationId();
      exchange = filterUtil.setCorrelationId(exchange, correlationId);
      log.debug("{} generated in tracking filter: {}", FilterUtil.CORRELATION_ID, correlationId);
    }
    return chain.filter(exchange);
  }

  private boolean isCorrelationIdPresent(HttpHeaders headers) {
    return !Objects.isNull(filterUtil.getCorrelationId(headers));
  }

  private String generateCorrelationId() {
    return UUID.randomUUID().toString();
  }
}
