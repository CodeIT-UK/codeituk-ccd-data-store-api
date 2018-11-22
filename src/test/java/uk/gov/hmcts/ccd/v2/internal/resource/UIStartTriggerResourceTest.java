package uk.gov.hmcts.ccd.v2.internal.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseEventTrigger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertAll;
import static uk.gov.hmcts.ccd.domain.service.common.TestBuildersUtil.CaseEventTriggerBuilder.anEventTrigger;
import static uk.gov.hmcts.ccd.domain.service.common.TestBuildersUtil.CaseViewFieldBuilder.aViewField;
import static uk.gov.hmcts.ccd.domain.service.common.TestBuildersUtil.WizardPageBuilder.aWizardPage;

@DisplayName("CaseResource")
class UIStartTriggerResourceTest {
    private static final String NAME = "eventName";
    private static final String DESCRIPTION = "eventDescription";
    private static final String TOKEN = "Token";
    private static final String END_BUTTON_LABEL = "Submit";
    private static final boolean IS_SHOW_SUMMARY = true;
    private static final boolean IS_SHOW_EVENT_NOTES = false;
    private static final boolean IS_SAVE_DRAFT = true;
    private static final String CASE_ID = "1111222233334444";
    private static final String FIELD_ID = "PersonFirstName";
    private static final String CASE_TYPE_ID = "TestAddressBookCase";
    private static final String EVENT_ID = "createCase";
    private static final String LINK_SELF = String.format("/internal/case-types/%s/triggers/%s?ignore-warning=true", CASE_TYPE_ID, EVENT_ID);

    private CaseEventTrigger caseEventTrigger;
    private boolean ignoreWarning;

    @BeforeEach
    void setUp() {
        caseEventTrigger = aCaseEventTrigger();
        ignoreWarning = true;
    }

    @Test
    @DisplayName("should copy case event trigger")
    void shouldCopyCaseDetails() {
        final UIStartTriggerResource uiStartTriggerResource = new UIStartTriggerResource(caseEventTrigger, CASE_TYPE_ID, ignoreWarning);

        assertAll(
            () -> assertThat(uiStartTriggerResource.getEventId(), equalTo(EVENT_ID)),
            () -> assertThat(uiStartTriggerResource.getName(), equalTo(NAME)),
            () -> assertThat(uiStartTriggerResource.getDescription(), equalTo(DESCRIPTION)),
            () -> assertThat(uiStartTriggerResource.getCaseId(), equalTo(CASE_ID)),
            () -> assertThat(uiStartTriggerResource.getCaseFields(), hasItems(hasProperty("id", is(FIELD_ID)))),
            () -> assertThat(uiStartTriggerResource.getEventToken(), equalTo(TOKEN)),
            () -> assertThat(uiStartTriggerResource.getWizardPages().get(0).getWizardPageFields().get(0), hasProperty("caseFieldId", is(FIELD_ID))),
            () -> assertThat(uiStartTriggerResource.getShowSummary(), equalTo(IS_SHOW_SUMMARY)),
            () -> assertThat(uiStartTriggerResource.getShowEventNotes(), equalTo(IS_SHOW_EVENT_NOTES)),
            () -> assertThat(uiStartTriggerResource.getEndButtonLabel(), equalTo(END_BUTTON_LABEL)),
            () -> assertThat(uiStartTriggerResource.getCanSaveDraft(), equalTo(IS_SAVE_DRAFT))
        );
    }

    @Test
    @DisplayName("should link to itself")
    void shouldLinkToSelf() {
        final UIStartTriggerResource uiStartTriggerResource = new UIStartTriggerResource(caseEventTrigger, CASE_TYPE_ID, ignoreWarning);

        assertThat(uiStartTriggerResource.getLink("self").getHref(), equalTo(LINK_SELF));
    }

    private CaseEventTrigger aCaseEventTrigger() {
        return anEventTrigger()
            .withId(EVENT_ID)
            .withName(NAME)
            .withDescription(DESCRIPTION)
            .withCaseId(CASE_ID)
            .withField(aViewField()
                           .withId(FIELD_ID).build())
            .withEventToken(TOKEN)
            .withWizardPage(aWizardPage()
                                .withField(aViewField()
                                               .withId(FIELD_ID)
                                               .build())
                                .build())
            .withShowSummary(IS_SHOW_SUMMARY)
            .withShowEventNotes(IS_SHOW_EVENT_NOTES)
            .withEndButtonLabel(END_BUTTON_LABEL)
            .withCanSaveDraft(IS_SAVE_DRAFT)
            .build();
    }
}
