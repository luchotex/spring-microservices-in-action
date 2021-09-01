package com.optimagrowth.license.service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.client.OrganizationDiscoveryClient;
import com.optimagrowth.license.service.client.OrganizationFeignClient;
import com.optimagrowth.license.service.client.OrganizationRestTemplateClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LicenseService {

  private MessageSource messages;
  private LicenseRepository licenseRepository;
  private ServiceConfig config;
  private OrganizationDiscoveryClient organizationDiscoveryClient;
  private OrganizationRestTemplateClient restTemplateClient;
  private OrganizationFeignClient feignClient;

  public LicenseService(
      MessageSource messages,
      LicenseRepository licenseRepository,
      ServiceConfig config,
      OrganizationDiscoveryClient organizationDiscoveryClient,
      OrganizationRestTemplateClient restTemplateClient,
      OrganizationFeignClient feignClient) {
    this.messages = messages;
    this.licenseRepository = licenseRepository;
    this.config = config;
    this.organizationDiscoveryClient = organizationDiscoveryClient;
    this.restTemplateClient = restTemplateClient;
    this.feignClient = feignClient;
  }

  public License getLicense(String licenseId, String organizationId) {

    License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
    if (Objects.isNull(license)) {
      throw new IllegalArgumentException(
          String.format(
              messages.getMessage("license.search.error.message", null, null),
              licenseId,
              organizationId));
    }
    return license.withComment(config.getExampleProperty());
  }

  public License createLicense(License license, String organizationId, Locale locale) {
    license.setLicenseId(UUID.randomUUID().toString());
    licenseRepository.save(license);

    return license.withComment(config.getExampleProperty());
  }

  public License updateLicense(License license, String organizationid /*, Locale locale*/) {
    licenseRepository.save(license);

    return license.withComment(config.getExampleProperty());
  }

  public String deleteLicense(String licenseId, String organizationId /*, Locale locale*/) {
    String responseMessage = null;
    License license = new License();
    license.setLicenseId(licenseId);
    licenseRepository.delete(license);
    responseMessage =
        String.format(
            messages.getMessage("license.delete.message", null, null /*locale*/),
            licenseId,
            organizationId);

    return responseMessage;
  }

  public License getLicense(String organizationId, String licenseId, String clientType) {

    License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
    if (Objects.isNull(license)) {
      throw new IllegalArgumentException(
          String.format(
              messages.getMessage("license.search.error.message", null, null),
              licenseId,
              organizationId));
    }
    Organization organization = retrieveOrganizationInfo(organizationId, clientType);

    if (!Objects.isNull(organization)) {
      license.setOrganizationName(organization.getName());
      license.setContactName(organization.getContactName());
      license.setContactEmail(organization.getContactEmail());
      license.setContactPhone(organization.getContactPhone());
    }

    return license.withComment(config.getExampleProperty());
  }

  @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
  @Bulkhead(name = "bulkheadLicenseService", fallbackMethod = "buildFallbackLicenseList")
  public List<License> getLicensesByOrganization(String organizationId) throws TimeoutException {
    randomlyRunLong();
    return licenseRepository.findByOrganizationId(organizationId);
  }

  @CircuitBreaker(name = "organizationService")
  private Organization retrieveOrganizationInfo(String organizationId, String clientType) {
    Organization result = null;

    switch (clientType) {
      case "feign":
        result = feignClient.getOrganization(organizationId);
        ;
        break;
      case "rest":
        result = restTemplateClient.getOrganization(organizationId);
        break;
      case "discovery":
        result = organizationDiscoveryClient.getOrganization(organizationId);
        break;
      default:
        result = restTemplateClient.getOrganization(organizationId);
        break;
    }

    return result;
  }

  private void randomlyRunLong() throws TimeoutException {
    Random rand = new Random();
    int randomNum = rand.nextInt(3) + 1;
    if (randomNum == 3) {
      sleep();
    }
  }

  private void sleep() throws TimeoutException {
    try {
      Thread.sleep(5000);
      throw new java.util.concurrent.TimeoutException();
    } catch (InterruptedException e) {
      log.error(e.getMessage());
    }
  }

  @SuppressWarnings("unused")
  private List<License> buildFallbackLicenseList(String organizationId, Throwable t) {
    List<License> result = new ArrayList<>();
    License license = new License();
    license.setLicenseId("0000000-00-00000");
    license.setOrganizationId(organizationId);
    license.setProductName("Sorry no licensing information currently available");
    result.add(license);

    return result;
  }
}
