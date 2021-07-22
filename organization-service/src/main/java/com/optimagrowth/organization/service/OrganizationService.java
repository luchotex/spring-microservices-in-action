package com.optimagrowth.organization.service;

import com.optimagrowth.organization.model.Organization;
import com.optimagrowth.organization.repository.OrganizationRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

  private OrganizationRepository repository;

  public OrganizationService(OrganizationRepository repository) {
    this.repository = repository;
  }

  public Organization findById(String organizationId) {
    Optional<Organization> result = repository.findById(organizationId);

    return result.isPresent() ? result.get() : null;
  }

  public Organization create(Organization organization) {
    organization.setId(UUID.randomUUID().toString());
    repository.save(organization);
    return organization;
  }

  public Organization update(Organization organization) {
    return repository.save(organization);
  }

  public void delete(Organization organization) {
    repository.deleteById(organization.getId());
  }
}
