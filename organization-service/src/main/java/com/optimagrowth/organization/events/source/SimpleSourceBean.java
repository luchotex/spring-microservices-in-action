package com.optimagrowth.organization.events.source;

import com.optimagrowth.organization.events.model.OrganizationChangeModel;
import com.optimagrowth.organization.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SimpleSourceBean {

  private Source source;

  public SimpleSourceBean(Source source) {
    this.source = source;
  }

  public void publishOrganizationChange(String action, String organizationId) {
    log.debug("Sending kafka message {} for Organization Id: {}", action, organizationId);

    OrganizationChangeModel change =
        new OrganizationChangeModel(
            OrganizationChangeModel.class.getTypeName(),
            action,
            organizationId,
            UserContext.getCorrelationId());

    source.output().send(MessageBuilder.withPayload(change).build());
  }
}
