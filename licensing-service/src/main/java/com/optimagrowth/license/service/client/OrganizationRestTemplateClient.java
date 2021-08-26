package com.optimagrowth.license.service.client;

import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.OrganizationRedisRepository;
import com.optimagrowth.license.utils.UserContext;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class OrganizationRestTemplateClient {

  private RestTemplate restTemplate;
  private OrganizationRedisRepository redisRepository;

  public OrganizationRestTemplateClient(
      RestTemplate restTemplate, OrganizationRedisRepository redisRepository) {
    this.restTemplate = restTemplate;
    this.redisRepository = redisRepository;
  }

  private Organization checkRedisCache(String organizationId) {
    try {
      return redisRepository.findById(organizationId).orElse(null);
    } catch (Exception ex) {
      log.error(
          "Error encountered while trying to retrieve organization {} check Redis Cache. Exception {}",
          organizationId,
          ex);
      return null;
    }
  }

  private void cacheOrganizationObject(Organization organization) {
    try {
      redisRepository.save(organization);
    } catch (Exception ex) {
      log.error("Unable to cache organization {} in Redis. Exception {}", organization.getId(), ex);
    }
  }

  public Organization getOrganization(String organizationId) {
    log.debug("In Licensing Service.getOrganization: {}", UserContext.getCorrelationId());

    Organization organization = checkRedisCache(organizationId);

    if (!Objects.isNull(organization)) {
      log.debug(
          "I have successfully retrieved an organization {} from the redis cache: {}",
          organizationId,
          organization);
      return organization;
    }

    log.debug("Unable to locate organization from redis cache: {}.", organizationId);

    ResponseEntity<Organization> restExchange =
        restTemplate.exchange(
            "http://gateway:8072/organization/v1/organization/{organizationId}",
            HttpMethod.GET,
            null,
            Organization.class,
            organizationId);
    organization = restExchange.getBody();

    if (!Objects.isNull(organization)) {
      cacheOrganizationObject(organization);
    }

    return organization;
  }
}
