package uk.nhs.cdss.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.Instant;

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
