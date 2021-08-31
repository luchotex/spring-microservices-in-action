package com.optimagrowth.gatewayserver.filters;

import brave.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class ResponseFilterGateway {

  @Autowired private Tracer tracer;

  @Autowired private FilterUtil filterUtil;

  @Bean
  public GlobalFilter postGlobalFilter() {
    return ((exchange, chain) -> {
      return chain
          .filter(exchange)
          .then(
              Mono.fromRunnable(
                  () -> {
                    String traceId = tracer.currentSpan().context().traceIdString();
                    log.debug("Adding the correlation id to the outbound headers. {}", traceId);
                    exchange.getResponse().getHeaders().add(FilterUtil.CORRELATION_ID, traceId);
                    log.debug(
                        "Completing outgoing request for {}.", exchange.getRequest().getURI());
                  }));
    });
  }
}
