package uk.nhs.cdss.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uk.nhs.cdss.audit.model.AuditSession;
import uk.nhs.cdss.service.AuditService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sqs")
public class SqsController {

  private final AuditService auditService;

  @PostMapping(path = "/send/{serviceName}")
  public void sendSQSMessage(@RequestBody AuditSession auditSession, @PathVariable String serviceName)
          throws JsonProcessingException {
    auditService.addAudit(auditSession, serviceName);
  }
}
