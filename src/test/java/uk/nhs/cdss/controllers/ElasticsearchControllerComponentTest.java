package uk.nhs.cdss.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.cdss.audit.model.AuditSession;
import uk.nhs.cdss.entities.AuditEntity;
import uk.nhs.cdss.repos.AuditRepository;

import java.time.Instant;
import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchControllerComponentTest {

  @Autowired
  private ElasticsearchController elasticsearchController;

  @Autowired
  private AuditRepository auditRepository;

  @Test
  public void findsAuditByCaseId() {
    var now = "1996-01-23T02:35:48Z";
    var then = "2020-09-22T11:53:41Z";

    var stored1 = AuditEntity.builder()
        .caseId("validCaseId1")
        .supplierId("validSupplierId1")
        .service("sameServiceName")
        .timestamp(Instant.parse(now))
        .serialisedAudit("{"
            + "\"requestUrl\":\"validRequestUrl1\","
            + "\"createdDate\":\"" + now + "\","
            + "\"responseBody\":\"validResponseBody1\","
            + "\"additionalProperties\":{"
              + "\"caseId\":\"validCaseId1\","
              + "\"supplierId\":\"validSupplierId1\""
            + "}"
            + "}")
        .build();
    var stored2 = AuditEntity.builder()
        .caseId("validCaseId2")
        .supplierId("validSupplierId2")
        .service("sameServiceName")
        .timestamp(Instant.parse(now))
        .serialisedAudit("{"
            + "\"requestUrl\":\"validRequestUrl2\","
            + "\"createdDate\":\"" + then + "\","
            + "\"responseBody\":\"validResponseBody2\","
            + "\"additionalProperties\":{"
              + "\"caseId\":\"validCaseId2\","
              + "\"supplierId\":\"validSupplierId2\""
            + "}"
            + "}")
        .build();

    auditRepository.saveAll(List.of(stored1, stored2));
    auditRepository.flush();


    var expectedAudit = AuditSession.builder()
        .requestUrl("validRequestUrl1")
        .createdDate(Instant.parse(now))
        .responseBody("validResponseBody1")
        .additionalProperty("caseId", "validCaseId1")
        .additionalProperty("supplierId", "validSupplierId1")
        .build();

    var audits = elasticsearchController.findAllByCaseId("validCaseId1");

    assertThat(audits, contains(sameBeanAs(expectedAudit).with("entries", nullValue())));
  }
}