package uk.nhs.cdss.audit.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a call to this server containing calls to other servers
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class AuditSession {

  String requestOrigin;
  String requestUrl;
  String requestMethod;
  String requestHeaders;
  String requestBody;

  String responseStatus;
  String responseHeaders;
  String responseBody;

  Instant createdDate;
  @Singular
  List<AuditEntry> entries;
  @Singular
  Map<String, String> additionalProperties;

}