package uk.nhs.cdss.audit.model;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Represents outgoing calls made by a web service during an audit session
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AuditEntry {

  String requestUrl;
  String requestMethod;
  String requestHeaders;
  String requestBody;

  String responseStatus;
  String responseHeaders;
  String responseBody;

  Instant dateOfEntry;

}
