package uk.nhs.cdss.service;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.InvalidParameterException;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.audit.model.AuditSession;
import uk.nhs.cdss.entities.AuditEntity;
import uk.nhs.cdss.repos.AuditRepository;
import uk.nhs.cdss.transformers.AuditEntityTransformer;

@RunWith(MockitoJUnitRunner.class)
public class AuditServiceTest {

  @InjectMocks
  private AuditService auditService;

  @Mock
  private AuditRepository auditRepository;

  @Mock
  private ObjectMapper mapper;

  @Mock
  private AuditEntityTransformer transformer;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void addAudit_withNullAudit_shouldFail() throws JsonProcessingException {
    expectedException.expect(NullPointerException.class);
    auditService.addAudit(null, "validServiceName");
  }

  @Test
  public void addAudit_withNullServiceName_shouldFail() throws JsonProcessingException {
    var validAudit = AuditSession.builder().build();

    expectedException.expect(NullPointerException.class);
    auditService.addAudit(validAudit, null);
  }

  @Test
  public void addAudit_withFailingTransformation_shouldFail() throws JsonProcessingException {
    var validAudit = AuditSession.builder().build();
    var validServiceName = "validServiceName";
    when(transformer.toEntity(validAudit, validServiceName))
        .thenThrow(JsonProcessingException.class);

    expectedException.expect(JsonProcessingException.class);
    auditService.addAudit(validAudit, validServiceName);
  }

  @Test
  public void addAudit_withValidAuditAndServiceName_shouldSaveAudit()
      throws JsonProcessingException {
    var validAudit = AuditSession.builder().build();
    var validServiceName = "validServiceName";

    var transformedEntity = AuditEntity.builder()
        .id(256L)
        .build();
    when(transformer.toEntity(validAudit, validServiceName))
        .thenReturn(transformedEntity);

    auditService.addAudit(validAudit, validServiceName);
    verify(auditRepository).saveAndFlush(transformedEntity);
  }

  @Test
  public void getAllByCaseId_withNullCaseId_shouldFail() {
    expectedException.expect(NullPointerException.class);
    auditService.getAllByCaseId(null);
  }

  @Test
  public void getAllByCaseId_withFailingSearch_shouldFail() {
    var caseId = "validCaseId";
    when(auditRepository.findAllByCaseId(caseId))
        .thenThrow(EntityNotFoundException.class);

    expectedException.expect(EntityNotFoundException.class);
    auditService.getAllByCaseId(caseId);
  }

  @Test
  public void getAllByCaseId_withFailingTransformation_shouldFail() {
    var caseId = "validCaseId";
    var auditEntity = AuditEntity.builder().caseId(caseId).id(1L).build();
    when(auditRepository.findAllByCaseId(caseId))
        .thenReturn(List.of(auditEntity));
    when(transformer.fromEntity(auditEntity))
        .thenThrow(InvalidParameterException.class);

    expectedException.expect(InvalidParameterException.class);
    auditService.getAllByCaseId(caseId);
  }

  @Test
  public void getAllByCaseId_withValidCaseId_shouldReturnSeveral() {
    var caseId = "validCaseId";
    var auditEntity1 = AuditEntity.builder().caseId(caseId).id(1L).build();
    var auditEntity2 = AuditEntity.builder().caseId(caseId).id(2L).build();
    when(auditRepository.findAllByCaseId(caseId))
        .thenReturn(List.of(auditEntity1, auditEntity2));

    var auditSession1 = AuditSession.builder().requestUrl("url1").build();
    var auditSession2 = AuditSession.builder().requestUrl("url2").build();
    when(transformer.fromEntity(auditEntity1)).thenReturn(auditSession1);
    when(transformer.fromEntity(auditEntity2)).thenReturn(auditSession2);

    var audits = auditService.getAllByCaseId(caseId);

    assertThat(audits, hasItems(auditSession1, auditSession2));
  }
}