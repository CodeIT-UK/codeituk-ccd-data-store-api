package uk.gov.hmcts.ccd.endpoint;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccd.data.casedetails.CaseDetailsEntity;
import uk.gov.hmcts.ccd.domain.service.doclink.DocLinksDetectionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DocLinksRestoreEndpoint {

    private final DocLinksDetectionService docLinksDetectionService;

    @Autowired
    public DocLinksRestoreEndpoint(DocLinksDetectionService docLinksDetectionService) {
        this.docLinksDetectionService = docLinksDetectionService;
    }

    @RequestMapping(value = "/doclinks/restore", method = RequestMethod.GET)
    public List<Long> findDocLinksMissedCases(
        @RequestParam(value = "jids", required = false) final String jurisdictionIds) {
        List<String> jurisdictionList = StringUtils.isNotEmpty(jurisdictionIds) ?
            Arrays.asList(jurisdictionIds.split(",")) : new ArrayList<>();
        List<CaseDetailsEntity> docLinksMissedCases = docLinksDetectionService.findDocLinksMissedCases(jurisdictionList);
        return docLinksMissedCases.stream().map(c -> c.getId()).collect(Collectors.toList());
    }
}

