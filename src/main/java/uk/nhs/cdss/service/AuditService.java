package uk.nhs.cdss.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.audit.model.AuditSession;
import uk.nhs.cdss.repos.AuditRepository;
import uk.nhs.cdss.transformers.AuditEntityTransformer;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

  @Qualifier("enhanced")
  private final ObjectMapper mapper;
  private final AuditRepository auditRepository;
  private final AuditEntityTransformer transformer;

  public void addAudit(AuditSession audit, String sendingService) throws JsonProcessingException {
    log.info(mapper.writeValueAsString(audit));

    auditRepository.saveAndFlush(transformer.toEntity(audit, sendingService));
  }

  public List<AuditSession> getAllByCaseId(String caseId) {
    log.info("Retrieving all audits for `caseId`: " + caseId);

    return auditRepository.findAllByCaseId(caseId)
            .stream()
            .map(transformer::fromEntity)
            .collect(Collectors.toUnmodifiableList());
  }
}
