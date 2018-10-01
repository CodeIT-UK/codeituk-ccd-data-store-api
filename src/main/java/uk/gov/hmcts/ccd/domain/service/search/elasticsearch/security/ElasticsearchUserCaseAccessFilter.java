package uk.gov.hmcts.ccd.domain.service.search.elasticsearch.security;

import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.ccd.data.casedetails.CaseDetailsEntity.STATE_FIELD_COL;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.domain.service.common.CaseAccessService;

@Component
public class ElasticsearchUserCaseAccessFilter implements CaseSearchFilter {

    private final CaseAccessService caseAccessService;

    @Autowired
    public ElasticsearchUserCaseAccessFilter(CaseAccessService caseAccessService) {
        this.caseAccessService = caseAccessService;
    }

    @Override
    public Optional<QueryBuilder> getFilter(String caseTypeId) {
        return getGrantedCaseIdsForRestrictedRoles().map(caseIds -> QueryBuilders.termsQuery(STATE_FIELD_COL, caseIds));
    }

    private Optional<List<Long>> getGrantedCaseIdsForRestrictedRoles() {
        return caseAccessService.getGrantedCaseIdsForRestrictedRoles();
    }
}
