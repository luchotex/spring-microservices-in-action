package com.optimagrowth.organization.service;

import com.optimagrowth.organization.events.source.SimpleSourceBean;
import com.optimagrowth.organization.model.Organization;
import com.optimagrowth.organization.repository.OrganizationRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrganizationService {

  private OrganizationRepository repository;
  private SimpleSourceBean simpleSourceBean;

  public OrganizationService(OrganizationRepository repository, SimpleSourceBean simpleSourceBean) {
    this.repository = repository;
    this.simpleSourceBean = simpleSourceBean;
  }

  public Organization findById(String organizationId) {
    Optional<Organization> result = repository.findById(organizationId);
    //    sleep();

    return result.isPresent() ? result.get() : null;
  }

  public Organization create(Organization organization) {
    organization.setId(UUID.randomUUID().toString());
    repository.save(organization);
    simpleSourceBean.publishOrganizationChange("SAVE", organization.getId());
    return organization;
  }

  public Organization update(Organization organization) {
    return repository.save(organization);
  }

  public void delete(Organization organization) {
    repository.deleteById(organization.getId());
  }

  private void sleep() {
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      log.error(e.getMessage());
    }
  }
}
