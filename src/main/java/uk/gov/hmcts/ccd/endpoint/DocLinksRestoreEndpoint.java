package uk.gov.hmcts.ccd.endpoint;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.ccd.data.casedetails.CaseDetailsEntity;
import uk.gov.hmcts.ccd.domain.service.doclink.DocLinksDetectionService;
import uk.gov.hmcts.ccd.domain.service.doclink.DocLinksRestoreService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DocLinksRestoreEndpoint {

    private final DocLinksDetectionService docLinksDetectionService;
    private final DocLinksRestoreService docLinksRestoreService;

    @Autowired
    public DocLinksRestoreEndpoint(DocLinksDetectionService docLinksDetectionService, DocLinksRestoreService docLinksRestoreService) {
        this.docLinksDetectionService = docLinksDetectionService;
        this.docLinksRestoreService = docLinksRestoreService;
    }

    @GetMapping(value = "/doclinks/restore")
    public List<Long> findDocLinksMissedCases(
        @RequestParam(value = "jids", required = false) final String jurisdictionIds) {
        List<String> jurisdictionList = StringUtils.isNotEmpty(jurisdictionIds)
            ? Arrays.asList(jurisdictionIds.split(",")) : new ArrayList<>();
        List<CaseDetailsEntity> docLinksMissedCases = docLinksDetectionService.findDocLinksMissedCases(jurisdictionList);
        return docLinksMissedCases.stream().map(c -> c.getId()).collect(Collectors.toList());
    }

    @PostMapping(value = "/doclinks/restore")
    public void restoreDocLinks(@RequestBody List<Long> caseReferences,
                                @RequestParam(value = "dryRun", required = false, defaultValue = "true") final Boolean dryRun) {
        if (dryRun) {
            docLinksRestoreService.restoreWithDryRun(caseReferences);
        } else {
            docLinksRestoreService.restoreWithPersist(caseReferences);
        }
    }
}

