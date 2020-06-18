package uk.nhs.cdss.controllers;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static uk.nhs.cdss.test.matchers.IsEqualJSON.equalToJSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.cdss.audit.model.AuditSession;
import uk.nhs.cdss.entities.AuditEntity;
import uk.nhs.cdss.repos.AuditRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SqsControllerComponentTest {

  @Autowired
  private SqsController sqsController;

  @Autowired
  private AuditRepository auditRepository;

  @Test
  public void savesAudit() throws JsonProcessingException {
    var now = "1996-01-23T02:35:48Z";
    var serviceName = "validServiceName";
    var auditSession = AuditSession.builder()
        .requestUrl("validRequestUrl")
        .createdDate(Instant.parse(now))
        .responseBody("validResponseBody")
        .additionalProperty("caseId", "validCaseId")
        .additionalProperty("supplierId", "validSupplierId")
        .build();

    sqsController.sendSQSMessage(auditSession, serviceName);

    var stored = auditRepository.findAllByCaseId("validCaseId");

    var expectedStored = AuditEntity.builder()
        .caseId("validCaseId")
        .supplierId("validSupplierId")
        .service("validServiceName")
        .timestamp(Instant.parse(now))
        .serialisedAudit("{"
            + "\"requestUrl\":\"validRequestUrl\","
            + "\"createdDate\":\"" + now + "\","
            + "\"responseBody\":\"validResponseBody\","
            + "\"additionalProperties\":{"
              + "\"caseId\":\"validCaseId\","
              + "\"supplierId\":\"validSupplierId\""
            + "}"
          + "}")
        .build();

    assertThat(stored, hasItem(
        sameBeanAs(expectedStored)
            .ignoring("id")
            .with("serialisedAudit", equalToJSON(expectedStored.getSerialisedAudit()))));
  }
}