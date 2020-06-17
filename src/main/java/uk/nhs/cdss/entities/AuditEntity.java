package uk.nhs.cdss.entities;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditEntity {
  @Id
  @GeneratedValue
  Long id;

  @Lob
  @Column(length = 100000)
  String serialisedAudit;

  String supplierId;
  String caseId;
  Instant timestamp;
  String service;
}
