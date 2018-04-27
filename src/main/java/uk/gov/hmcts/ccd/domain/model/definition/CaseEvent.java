package uk.gov.hmcts.ccd.domain.model.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import uk.gov.hmcts.ccd.data.casedetails.SecurityClassification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CaseEvent implements Serializable {

    private String id = null;
    private String name = null;
    private String description = null;
    @JsonProperty("order")
    private Integer displayOrder = null;
    @JsonProperty("case_fields")
    private List<CaseEventField> caseFields = new ArrayList<>();
    @JsonProperty("pre_states")
    private List<String> preStates = new ArrayList<>();
    @JsonProperty("post_state")
    private String postState = null;
    @JsonProperty("callback_url_about_to_start_event")
    private String callBackURLAboutToStartEvent;
    @JsonProperty("retries_timeout_about_to_start_event")
    private List<Integer> retriesTimeoutAboutToStartEvent;
    @JsonProperty("callback_url_about_to_submit_event")
    private String callBackURLAboutToSubmitEvent;
    @JsonProperty("retries_timeout_url_about_to_submit_event")
    private List<Integer> retriesTimeoutURLAboutToSubmitEvent;
    @JsonProperty("callback_url_submitted_event")
    private String callBackURLSubmittedEvent;
    @JsonProperty("retries_timeout_url_submitted_event")
    private List<Integer> retriesTimeoutURLSubmittedEvent;
    @JsonProperty("security_classification")
    private SecurityClassification securityClassification;
    @JsonProperty("show_summary")
    private Boolean showSummary = null;
    @JsonProperty("acls")
    private List<AccessControlList> accessControlLists;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<CaseEventField> getCaseFields() {
        return caseFields;
    }

    public void setCaseFields(List<CaseEventField> caseFields) {
        this.caseFields = caseFields;
    }

    public List<String> getPreStates() {
        return preStates;
    }

    public void setPreStates(List<String> preStates) {
        this.preStates = preStates;
    }

    public String getPostState() {
        return postState;
    }

    public void setPostState(String postState) {
        this.postState = postState;
    }

    public String getCallBackURLAboutToStartEvent() {
        return callBackURLAboutToStartEvent;
    }

    public void setCallBackURLAboutToStartEvent(String callBackURLAboutToStartEvent) {
        this.callBackURLAboutToStartEvent = callBackURLAboutToStartEvent;
    }

    public List<Integer> getRetriesTimeoutAboutToStartEvent() {
        return retriesTimeoutAboutToStartEvent;
    }

    public void setRetriesTimeoutAboutToStartEvent(List<Integer> retriesTimeoutAboutToStartEvent) {
        this.retriesTimeoutAboutToStartEvent = retriesTimeoutAboutToStartEvent;
    }

    public String getCallBackURLAboutToSubmitEvent() {
        return callBackURLAboutToSubmitEvent;
    }

    public void setCallBackURLAboutToSubmitEvent(String callBackURLAboutToSubmitEvent) {
        this.callBackURLAboutToSubmitEvent = callBackURLAboutToSubmitEvent;
    }

    public List<Integer> getRetriesTimeoutURLAboutToSubmitEvent() {
        return retriesTimeoutURLAboutToSubmitEvent;
    }

    public void setRetriesTimeoutURLAboutToSubmitEvent(List<Integer> retriesTimeoutURLAboutToSubmitEvent) {
        this.retriesTimeoutURLAboutToSubmitEvent = retriesTimeoutURLAboutToSubmitEvent;
    }

    public String getCallBackURLSubmittedEvent() {
        return callBackURLSubmittedEvent;
    }

    public void setCallBackURLSubmittedEvent(String callBackURLSubmittedEvent) {
        this.callBackURLSubmittedEvent = callBackURLSubmittedEvent;
    }

    public List<Integer> getRetriesTimeoutURLSubmittedEvent() {
        return retriesTimeoutURLSubmittedEvent;
    }

    public void setRetriesTimeoutURLSubmittedEvent(List<Integer> retriesTimeoutURLSubmittedEvent) {
        this.retriesTimeoutURLSubmittedEvent = retriesTimeoutURLSubmittedEvent;
    }

    public SecurityClassification getSecurityClassification() {
        return securityClassification;
    }

    public void setSecurityClassification(SecurityClassification securityClassification) {
        this.securityClassification = securityClassification;
    }


    public List<AccessControlList> getAccessControlLists() {
        return accessControlLists;
    }

    public void setAccessControlLists(List<AccessControlList> accessControlLists) {
        this.accessControlLists = accessControlLists;
    }

    public Boolean getShowSummary() {
        return showSummary;
    }

    public void setShowSummary(final Boolean showSummary) {
        this.showSummary = showSummary;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("name", name)
            .append("description", description)
            .append("displayOrder", displayOrder)
            .append("caseFields", caseFields)
            .append("preStates", preStates)
            .append("postState", postState)
            .append("callBackURLAboutToStartEvent", callBackURLAboutToStartEvent)
            .append("retriesTimeoutAboutToStartEvent", retriesTimeoutAboutToStartEvent)
            .append("callBackURLAboutToSubmitEvent", callBackURLAboutToSubmitEvent)
            .append("retriesTimeoutURLAboutToSubmitEvent", retriesTimeoutURLAboutToSubmitEvent)
            .append("callBackURLSubmittedEvent", callBackURLSubmittedEvent)
            .append("retriesTimeoutURLSubmittedEvent", retriesTimeoutURLSubmittedEvent)
            .append("securityClassification", securityClassification)
            .append("showSummary", showSummary)
            .append("accessControlLists", accessControlLists)
            .toString();
    }
}
