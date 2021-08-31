package com.optimagrowth.organization.service;

import brave.ScopedSpan;
import brave.Tracer;
import com.optimagrowth.organization.events.source.SimpleSourceBean;
import com.optimagrowth.organization.model.Organization;
import com.optimagrowth.organization.repository.OrganizationRepository;
import com.optimagrowth.organization.utils.ActionEnum;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrganizationService {

  private OrganizationRepository repository;
  private SimpleSourceBean simpleSourceBean;
  private Tracer tracer;

  public OrganizationService(
      OrganizationRepository repository, SimpleSourceBean simpleSourceBean, Tracer tracer) {
    this.repository = repository;
    this.simpleSourceBean = simpleSourceBean;
    this.tracer = tracer;
  }

  public Organization findById(String organizationId) {
    Optional<Organization> result = null;
    ScopedSpan newSpan = tracer.startScopedSpan("getOrgDBCall");
    try {
      result = repository.findById(organizationId);
      simpleSourceBean.publishOrganizationChange(ActionEnum.GET, organizationId);
      if (!result.isPresent()) {
        String message =
            String.format(
                "Unable to find and organization with the organization id %s", organizationId);
        log.error(message);
        throw new IllegalArgumentException(message);
      }
      log.debug("Retrieving Organization Info: {}", result.get());
    } finally {
      newSpan.tag("peer.service", "postgres");
      newSpan.annotate("Cliente received");
      newSpan.finish();
    }
    //    sleep();

    return result.get();
  }

  public Organization create(Organization organization) {
    organization.setId(UUID.randomUUID().toString());
    repository.save(organization);
    simpleSourceBean.publishOrganizationChange(ActionEnum.CREATE, organization.getId());
    return organization;
  }

  public Organization update(Organization organization) {
    simpleSourceBean.publishOrganizationChange(ActionEnum.UPDATE, organization.getId());
    return repository.save(organization);
  }

  public void delete(Organization organization) {
    repository.deleteById(organization.getId());
    simpleSourceBean.publishOrganizationChange(ActionEnum.DELETE, organization.getId());
  }

  private void sleep() {
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      log.error(e.getMessage());
    }
  }
}
