package uk.nhs.cdss.transformers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.audit.model.AuditSession;
import uk.nhs.cdss.entities.AuditEntity;

@Component
@RequiredArgsConstructor
public class AuditEntityTransformer {

    private static final String SUPPLIER_ID = "supplierId";
    private static final String CASE_ID = "caseId";

    private final ObjectMapper mapper;

    public AuditEntity toEntity(AuditSession audit, String sendingService) throws JsonProcessingException {
        Preconditions.checkNotNull(audit);
        Preconditions.checkNotNull(sendingService);

        return AuditEntity.builder()
                .serialisedAudit(mapper.writeValueAsString(audit))
                .service(sendingService)
                .caseId(audit.getAdditionalProperties().get(CASE_ID))
                .supplierId(audit.getAdditionalProperties().get(SUPPLIER_ID))
                .timestamp(audit.getCreatedDate())
                .build();
    }

    // throw JSON exceptions at runtime because they would be delegated to the controller method anyway
    // also, having the exceptions compile-time checked hinders using this for higher-order functions
    @SneakyThrows
    public AuditSession fromEntity(AuditEntity entity) {
        Preconditions.checkNotNull(entity.getSerialisedAudit());

        return mapper.readValue(entity.getSerialisedAudit(), AuditSession.class);
    }
}
