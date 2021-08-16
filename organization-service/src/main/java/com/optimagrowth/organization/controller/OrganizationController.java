package com.optimagrowth.organization.controller;

import com.optimagrowth.organization.model.Organization;
import com.optimagrowth.organization.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/organization")
public class OrganizationController {

  private OrganizationService organizationService;

  public OrganizationController(OrganizationService organizationService) {
    this.organizationService = organizationService;
  }

  @GetMapping(value = "/{organizationId}")
  public ResponseEntity<Organization> findById(
      @PathVariable(name = "organizationId") String organizationId) {
    return ResponseEntity.ok(organizationService.findById(organizationId));
  }

  @PostMapping
  public ResponseEntity<Organization> create(@RequestBody Organization organization) {
    return ResponseEntity.ok(organizationService.create(organization));
  }

  @PutMapping(value = "/{organizationId}")
  public void update(
      @PathVariable("organizationId") String organizationId,
      @RequestBody Organization organization) {
    organizationService.update(organization);
  }

  @DeleteMapping(value = "/{organizationId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @PathVariable("organizationId") String organizationId,
      @RequestBody Organization organization) {
    organizationService.delete(organization);
  }
}
