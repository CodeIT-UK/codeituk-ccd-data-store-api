package uk.gov.hmcts.ccd.domain.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CaseTypesResults {

    @JsonProperty("case_field_id")
    private final String caseTypeId;
    private final long total;

    public CaseTypesResults(String caseFieldId, long numberOfMatchedCases) {

        this.caseTypeId = caseFieldId;
        this.total = numberOfMatchedCases;
    }

    public String getCaseTypeId() {
        return caseTypeId;
    }

    public long getTotal() {
        return total;
    }
}
