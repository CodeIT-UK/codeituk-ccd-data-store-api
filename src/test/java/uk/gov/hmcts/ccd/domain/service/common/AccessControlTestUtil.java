package uk.gov.hmcts.ccd.domain.service.common;

import com.google.common.collect.Lists;
import uk.gov.hmcts.ccd.domain.model.aggregated.*;
import uk.gov.hmcts.ccd.domain.model.definition.*;
import uk.gov.hmcts.ccd.domain.model.search.Field;
import uk.gov.hmcts.ccd.domain.model.search.SearchInput;
import uk.gov.hmcts.ccd.domain.model.search.WorkbasketInput;
import uk.gov.hmcts.ccd.domain.model.std.AuditEvent;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class AccessControlTestUtil {

    public static class CaseTypeBuilder {
        private final CaseType caseType;
        private final List<AccessControlList> acls = newArrayList();
        private CaseTypeBuilder() {
            this.caseType = new CaseType();
            caseType.setJurisdiction(new Jurisdiction());
        }

        public CaseTypeBuilder withId(String id) {
            caseType.setId(id);
            return this;

        }        public CaseTypeBuilder withJurisdiction(Jurisdiction jurisdiction) {
            caseType.setJurisdiction(jurisdiction);
            return this;
        }

        public CaseTypeBuilder withCaseTypeId(String caseTypeId) {
            caseType.setId(caseTypeId);
            return this;
        }

        public CaseTypeBuilder withEvent(CaseEvent event) {
            caseType.getEvents().add(event);
            return this;
        }

        public CaseTypeBuilder withState(CaseState state) {
            caseType.getStates().add(state);
            return this;
        }

        public CaseTypeBuilder withField(CaseField field) {
            caseType.getCaseFields().add(field);
            return this;
        }

        public CaseTypeBuilder withAcl(AccessControlList accessControlList) {
            this.acls.add(accessControlList);
            return this;
        }

        public CaseType build() {
            caseType.setAccessControlLists(this.acls);
            return caseType;
        }

        public static CaseTypeBuilder aCaseType() {
            return new CaseTypeBuilder();
        }
    }

    public static class CaseViewBuilder {
        private final CaseView caseView;
        private final List<CaseViewTrigger> caseViewTriggers = newArrayList();
        private CaseViewBuilder() {
            this.caseView = new CaseView();
        }

        public CaseViewBuilder withCaseViewTrigger(CaseViewTrigger caseViewTrigger) {
            this.caseViewTriggers.add(caseViewTrigger);
            return this;
        }

        public CaseViewBuilder withState(ProfileCaseState state) {
            caseView.setState(state);
            return this;
        }

        public CaseView build() {
            caseView.setTriggers(caseViewTriggers.toArray(new CaseViewTrigger[]{}));
            return caseView;
        }

        public static CaseViewBuilder aCaseView() {
            return new CaseViewBuilder();
        }
    }

    public static class CaseStateBuilder {
        private final CaseState caseState;
        private final List<AccessControlList> acls = newArrayList();
        private CaseStateBuilder() {
            this.caseState = new CaseState();
        }

        public CaseStateBuilder withAcl(AccessControlList accessControlList) {
            this.acls.add(accessControlList);
            return this;
        }

        public CaseStateBuilder withId(String id) {
            caseState.setId(id);
            return this;
        }

        public CaseState build() {
            caseState.setAccessControlLists(this.acls);
            return caseState;
        }

        public static CaseStateBuilder aState() {
            return new CaseStateBuilder();
        }
    }

    public static class AccessControlListBuilder {
        private final AccessControlList accessControlList;

        private AccessControlListBuilder() {
            this.accessControlList = new AccessControlList();
        }

        public AccessControlListBuilder withRole(String role) {
            this.accessControlList.setRole(role);
            return this;
        }

        public AccessControlListBuilder withCreate(boolean create) {
            this.accessControlList.setCreate(create);
            return this;
        }

        public AccessControlListBuilder withUpdate(boolean update) {
            this.accessControlList.setUpdate(update);
            return this;
        }

        public AccessControlListBuilder withRead(boolean read) {
            this.accessControlList.setRead(read);
            return this;
        }

        public AccessControlList build() {
            return accessControlList;
        }

        public static AccessControlListBuilder anAcl() {
            return new AccessControlListBuilder();
        }
    }

    public static class CaseEventBuilder {
        private final CaseEvent caseEvent;
        private final List<AccessControlList> accessControlLists = newArrayList();
        private CaseEventBuilder() {
            this.caseEvent = new CaseEvent();
        }

        public CaseEventBuilder withAcl(AccessControlList accessControlList) {
            accessControlLists.add(accessControlList);
            return this;
        }

        public CaseEventBuilder withId(String id) {
            caseEvent.setId(id);
            return this;
        }

        public CaseEvent build() {
            caseEvent.setAccessControlLists(accessControlLists);
            return caseEvent;
        }

        public static CaseEventBuilder anEvent() {
            return new CaseEventBuilder();
        }
    }

    public static class CaseViewTriggerBuilder {
        private final CaseViewTrigger caseViewTrigger;
        private CaseViewTriggerBuilder() {
            this.caseViewTrigger = new CaseViewTrigger();
        }

        public CaseViewTriggerBuilder withId(String id) {
            caseViewTrigger.setId(id);
            return this;
        }

        public CaseViewTrigger build() {
            return caseViewTrigger;
        }

        public static CaseViewTriggerBuilder aViewTrigger() {
            return new CaseViewTriggerBuilder();
        }
    }

    public static class CaseEventTriggerBuilder {
        private final CaseEventTrigger caseEventTrigger;
        private final List<CaseViewField> caseFields = Lists.newArrayList();
        private List<WizardPage> wizardPages = Lists.newArrayList();

        private CaseEventTriggerBuilder() {
            this.caseEventTrigger = new CaseEventTrigger();
        }

        public CaseEventTriggerBuilder withId(String id) {
            caseEventTrigger.setId(id);
            return this;
        }

        public CaseEventTriggerBuilder withWizardPage(WizardPage wizardPage) {
            this.wizardPages.add(wizardPage);
            return this;
        }

        public CaseEventTriggerBuilder withField(CaseViewField caseField) {
            caseFields.add(caseField);
            return this;
        }

        public static CaseEventTriggerBuilder anEventTrigger() {
            return new CaseEventTriggerBuilder();
        }

        public CaseEventTrigger build() {
            this.caseEventTrigger.setCaseFields(this.caseFields);
            caseEventTrigger.setWizardPages(this.wizardPages);
            return caseEventTrigger;
        }
    }

    public static class WizardPageBuilder {
        private final WizardPage wizardPage;
        private final List<WizardPageField> wizardPageFields = Lists.newArrayList();
        private WizardPageBuilder() {
            this.wizardPage = new WizardPage();
        }

        public WizardPageBuilder withId(String id) {
            wizardPage.setId(id);
            return this;
        }

        public WizardPageBuilder withField(CaseViewField caseField) {
            WizardPageField wizardPageField = new WizardPageField();
            wizardPageField.setCaseFieldId(caseField.getId());
            wizardPageField.setPageColumnNumber(1);
            wizardPageField.setOrder(1);
            wizardPageFields.add(wizardPageField);
            return this;
        }

        static WizardPageBuilder aWizardPage() {
            return new WizardPageBuilder();
        }

        public WizardPage build() {
            this.wizardPage.setWizardPageFields(this.wizardPageFields);
            return wizardPage;
        }
    }

    public static class CaseFieldBuilder {
        private final CaseField caseField;
        private final List<AccessControlList> accessControlLists = newArrayList();
        private CaseFieldBuilder() {
            this.caseField = new CaseField();
        }

        public CaseFieldBuilder withId(String id) {
            caseField.setId(id);
            return this;
        }

        public CaseFieldBuilder withSC(String securityClassification) {
            caseField.setSecurityLabel(securityClassification);
            return this;
        }

        public CaseFieldBuilder withAcl(AccessControlList accessControlList) {
            accessControlLists.add(accessControlList);
            return this;
        }

        public CaseField build() {
            caseField.setAccessControlLists(accessControlLists);
            return caseField;
        }

        public static CaseFieldBuilder aCaseField() {
            return new CaseFieldBuilder();
        }
    }

    public static class CaseViewFieldBuilder {
        private final CaseViewField caseViewField;
        private CaseViewFieldBuilder() {
            this.caseViewField = new CaseViewField();
        }

        public CaseViewFieldBuilder withId(String id) {
            caseViewField.setId(id);
            return this;
        }

        public CaseViewField build() {
            return caseViewField;
        }

        public static CaseViewFieldBuilder aViewField() {
            return new CaseViewFieldBuilder();
        }
    }

    public static class AuditEventBuilder {
        private final AuditEvent auditEvent;
        private AuditEventBuilder() {
            this.auditEvent = new AuditEvent();
        }

        public AuditEventBuilder withEventId(String id) {
            auditEvent.setEventId(id);
            return this;
        }

        public AuditEvent build() {
            return auditEvent;
        }

        public static AuditEventBuilder anAuditEvent() {
            return new AuditEventBuilder();
        }
    }


    public static class WorkbasketInputBuilder {
        private final WorkbasketInput workbasketInput;

        private WorkbasketInputBuilder() {
            this.workbasketInput = new WorkbasketInput();
        }

        public WorkbasketInputBuilder withFieldId(String fieldId){
            Field f = new Field();
            f.setId(fieldId);
            this.workbasketInput.setField(f);
            return this;
        }

        public WorkbasketInput build() {
            return this.workbasketInput;
        }

        public static WorkbasketInputBuilder aWorkbasketInput() {
            return new WorkbasketInputBuilder();
        }
    }

    public static class SearchInputBuilder {
        private final SearchInput searchInput;

        private SearchInputBuilder() {
            this.searchInput = new SearchInput();
        }

        public SearchInputBuilder withFieldId(String fieldId){
            Field f = new Field();
            f.setId(fieldId);
            this.searchInput.setField(f);
            return this;
        }

        public SearchInput build() {
            return this.searchInput;
        }

        public static SearchInputBuilder aSearchInput() {
            return new SearchInputBuilder();
        }
    }
}
