package com.optimagrowth.license.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.service.LicenseService;
import java.util.List;
import java.util.Locale;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "v1/organization/{organizationId}/license")
public class LicenseController {

  private LicenseService licenseService;

  public LicenseController(LicenseService licenseService) {
    this.licenseService = licenseService;
  }

  @GetMapping(value = "/{licenseId}")
  public ResponseEntity<License> getLicense(
      @PathVariable("organizationId") String organizationId,
      @PathVariable("licenseId") String licenseId) {
    License license = licenseService.getLicense(licenseId, organizationId);

    license.add(
        linkTo(methodOn(LicenseController.class).getLicense(organizationId, license.getLicenseId()))
            .withSelfRel(),
        linkTo(methodOn(LicenseController.class).createLicense(organizationId, license, null))
            .withRel("createLicense"),
        linkTo(methodOn(LicenseController.class).updateLicense(organizationId, license))
            .withRel("updateLicense"),
        linkTo(methodOn(LicenseController.class).deleteLicense(organizationId, licenseId))
            .withRel("deleteLicense"));

    return ResponseEntity.ok(license);
  }

  @PostMapping
  public ResponseEntity<License> createLicense(
      @PathVariable("organizationId") String organizationId,
      @RequestBody License license,
      @RequestHeader(value = "Accept-Language", required = false) Locale locale) {
    return ResponseEntity.ok(licenseService.createLicense(license, organizationId, locale));
  }

  @PutMapping
  public ResponseEntity<License> updateLicense(
      @PathVariable("organizationId") String organizationId, @RequestBody License license
      /*, Locale locale*/ ) {
    return ResponseEntity.ok(licenseService.updateLicense(license, organizationId /*, locale*/));
  }

  @DeleteMapping(value = "/{licenseId}")
  public ResponseEntity<String> deleteLicense(
      @PathVariable("organizationId") String organizationId,
      @PathVariable("licenseId") String licenseId /*, Locale locale*/) {
    return ResponseEntity.ok(licenseService.deleteLicense(organizationId, licenseId /*, locale*/));
  }

  @GetMapping(value = "/{licenseId}/{clientType}")
  public License getLicenseByClient(
      @PathVariable("organizationId") String organizationId,
      @PathVariable("licenseId") String licenseId,
      @PathVariable("clientType") String clientType) {
    return licenseService.getLicense(organizationId, licenseId, clientType);
  }

  @GetMapping(value = "/")
  public List<License> getLicenses(@PathVariable("organizationId") String organizationId) {
    return licenseService.getLicensesByOrganization(organizationId);
  }
}
