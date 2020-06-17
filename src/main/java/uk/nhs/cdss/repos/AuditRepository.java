package uk.nhs.cdss.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.cdss.entities.AuditEntity;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntity, String> {
    List<AuditEntity> findAllByCaseId(String caseId);
}
