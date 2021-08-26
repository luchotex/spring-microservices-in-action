package com.optimagrowth.organization.utils;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserContextFilter implements Filter {

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {

    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

    UserContextHolder.getContext()
        .setCorrelationId(httpServletRequest.getHeader(UserContext.CORRELATION_ID));
    UserContextHolder.getContext().setUserId(httpServletRequest.getHeader(UserContext.USER_ID));
    UserContextHolder.getContext()
        .setAuthToken(httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
    UserContextHolder.getContext()
        .setOrganizationId(httpServletRequest.getHeader(UserContext.ORGANIZATION_ID));

    log.debug(
        "Organization Service Incoming Correlation id: {}",
        UserContextHolder.getContext().getCorrelationId());

    log.debug("UserContextFilter Authorization: {}", UserContextHolder.getContext().getAuthToken());
    filterChain.doFilter(httpServletRequest, servletResponse);
  }
}
