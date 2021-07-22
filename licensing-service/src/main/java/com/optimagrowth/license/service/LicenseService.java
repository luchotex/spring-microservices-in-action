package com.optimagrowth.license.service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.client.OrganizationDiscoveryClient;
import com.optimagrowth.license.service.client.OrganizationFeignClient;
import com.optimagrowth.license.service.client.OrganizationRestTemplateClient;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
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
}
