package uk.nhs.cdss.transformers;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.audit.model.AuditSession;
import uk.nhs.cdss.entities.AuditEntity;

@RunWith(MockitoJUnitRunner.class)
public class AuditEntityTransformerTest {

  @InjectMocks
  private AuditEntityTransformer transformer;

  @Mock
  private ObjectMapper mapper;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void toEntity_withNullAudit_shouldFail() throws JsonProcessingException {
    expectedException.expect(NullPointerException.class);
    transformer.toEntity(null, "validServiceName");
  }

  @Test
  public void toEntity_withNullSendingService_shouldFail() throws JsonProcessingException {
    var validAudit = AuditSession.builder().build();

    expectedException.expect(NullPointerException.class);
    transformer.toEntity(validAudit, null);
  }

  @Test
  public void toEntity_withFailedMapping_shouldFail() throws JsonProcessingException {
    var validAudit = AuditSession.builder().build();
    var validServiceName = "validServiceName";
    when(mapper.writeValueAsString(validAudit))
        .thenThrow(JsonProcessingException.class);

    expectedException.expect(JsonProcessingException.class);
    transformer.toEntity(validAudit, validServiceName);
  }

  @Test
  public void toEntity_withValidAuditAndServiceName_shouldTransformAudit()
      throws JsonProcessingException {
    var now = Instant.now();
    var validAudit = AuditSession.builder()
        .createdDate(now)
        .additionalProperty("caseId", "validCaseId")
        .additionalProperty("supplierId", "validSupplierId")
        .build();
    when(mapper.writeValueAsString(validAudit)).thenReturn("serialisedAudit");

    var entity = transformer.toEntity(validAudit, "validServiceName");

    var expectedEntity = AuditEntity.builder()
        .serialisedAudit("serialisedAudit")
        .caseId("validCaseId")
        .supplierId("validSupplierId")
        .timestamp(now)
        .service("validServiceName")
        .build();
    assertThat(entity, sameBeanAs(expectedEntity));
  }

  @Test
  public void fromEntity_withNullAudit_shouldFail() {
    expectedException.expect(NullPointerException.class);
    //noinspection ConstantConditions
    transformer.fromEntity(null);
  }

  @Test
  public void fromEntity_withNullSerialisedAudit_shouldFail() {
    var invalidAuditEntity = AuditEntity.builder()
        .serialisedAudit(null)
        .build();

    expectedException.expect(NullPointerException.class);
    transformer.fromEntity(invalidAuditEntity);
  }

  @Test
  public void fromEntity_withValidAuditEntity_shouldTransformEntity() throws IOException {
    var serialisedAudit = "serialisedAudit";
    var validAuditEntity = AuditEntity.builder()
        .serialisedAudit("serialisedAudit")
        .build();

    var expectedAuditSession = AuditSession.builder()
        .requestUrl("validRequestUrl")
        .build();

    when(mapper.readValue(serialisedAudit, AuditSession.class))
        .thenReturn(expectedAuditSession);


    var auditSession = transformer.fromEntity(validAuditEntity);

    assertThat(auditSession, sameBeanAs(expectedAuditSession));
  }
}