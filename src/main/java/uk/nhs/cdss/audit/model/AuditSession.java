package uk.nhs.cdss.audit.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.FieldDefaults;

/**
 * Represents an incoming call to a particular server
 * containing outgoing calls from that server throughout the duration of that incoming call.
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