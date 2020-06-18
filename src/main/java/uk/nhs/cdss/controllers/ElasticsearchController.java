package uk.nhs.cdss.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.cdss.audit.model.AuditSession;
import uk.nhs.cdss.service.AuditService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/es")
public class ElasticsearchController {

    private final AuditService auditService;

    @GetMapping(path = "/caseId/{caseId}")
    public List<AuditSession> findAllByCaseId(@PathVariable String caseId) {
        return auditService.getAllByCaseId(caseId);
    }
}
