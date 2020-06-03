package uk.gov.hmcts.ccd.domain.model.search.elasticsearch;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UICaseSearchResultTest {

    private static final String JURISDICTION = "Jurisdiction";
    private static final String CASE_TYPE = "CaseTypeA";

    @Test
    void shouldFindHeaderByCaseType() {
        SearchResultViewHeaderGroup correctHeader = new SearchResultViewHeaderGroup(
            new HeaderGroupMetadata(JURISDICTION, CASE_TYPE), emptyList(), emptyList()
        );
        SearchResultViewHeaderGroup otherHeader = new SearchResultViewHeaderGroup(
            new HeaderGroupMetadata(JURISDICTION, "Other Case Type"), emptyList(), emptyList()
        );
        UICaseSearchResult uiCaseSearchResult = new UICaseSearchResult(Arrays.asList(otherHeader, correctHeader), emptyList(), 0L, null);

        Optional<SearchResultViewHeaderGroup> result = uiCaseSearchResult.findHeaderByCaseType(CASE_TYPE);

        assertAll(
            () -> assertTrue(result.isPresent()),
            () -> assertThat(result.get(), is(correctHeader))
        );
    }

    @Test
    void shouldNotFindNonExistingHeader() {
        SearchResultViewHeaderGroup header = new SearchResultViewHeaderGroup(
            new HeaderGroupMetadata(JURISDICTION, CASE_TYPE), emptyList(), emptyList()
        );
        UICaseSearchResult uiCaseSearchResult = new UICaseSearchResult(Collections.singletonList(header), emptyList(), 0L, null);

        Optional<SearchResultViewHeaderGroup> result = uiCaseSearchResult.findHeaderByCaseType("Other Case Type");

        assertAll(
            () -> assertFalse(result.isPresent())
        );
    }

    @Test
    void shouldFindCaseByReference() {
        SearchResultViewItem item1 = new SearchResultViewItem("111", Collections.emptyMap(), Collections.emptyMap());
        SearchResultViewItem item2 = new SearchResultViewItem("222", Collections.emptyMap(), Collections.emptyMap());
        UICaseSearchResult uiCaseSearchResult = new UICaseSearchResult(emptyList(), Arrays.asList(item1, item2), 0L, null);

        Optional<SearchResultViewItem> result = uiCaseSearchResult.findCaseByReference("222");

        assertAll(
            () -> assertTrue(result.isPresent()),
            () -> assertThat(result.get(), is(item2))
        );
    }

    @Test
    void shouldNotFindNonExistingCase() {
        SearchResultViewItem item = new SearchResultViewItem("111", Collections.emptyMap(), Collections.emptyMap());
        UICaseSearchResult uiCaseSearchResult = new UICaseSearchResult(emptyList(), Collections.singletonList(item), 0L, null);

        Optional<SearchResultViewItem> result = uiCaseSearchResult.findCaseByReference("000");

        assertAll(
            () -> assertFalse(result.isPresent())
        );
    }

    @Test
    void shouldFindCasesByCaseType() {
        SearchResultViewItem correctItem1 = new SearchResultViewItem("111", Collections.emptyMap(), Collections.emptyMap());
        SearchResultViewItem correctItem2 = new SearchResultViewItem("222", Collections.emptyMap(), Collections.emptyMap());
        SearchResultViewItem otherItem3 = new SearchResultViewItem("333", Collections.emptyMap(), Collections.emptyMap());
        SearchResultViewHeaderGroup correctHeader = new SearchResultViewHeaderGroup(
            new HeaderGroupMetadata(JURISDICTION, CASE_TYPE), emptyList(), Arrays.asList("111", "222")
        );
        SearchResultViewHeaderGroup otherHeader = new SearchResultViewHeaderGroup(
            new HeaderGroupMetadata(JURISDICTION, "Other Case Type"), emptyList(), Arrays.asList("333")
        );
        UICaseSearchResult uiCaseSearchResult = new UICaseSearchResult(
            Arrays.asList(otherHeader, correctHeader), Arrays.asList(correctItem1, otherItem3, correctItem2), 0L, null
        );

        List<SearchResultViewItem> result = uiCaseSearchResult.findCasesByCaseType(CASE_TYPE);

        assertAll(
            () -> assertThat(result.size(), is(2)),
            () -> assertThat(result, hasItem(correctItem1)),
            () -> assertThat(result, hasItem(correctItem2))
        );
    }

    @Test
    void shouldReturnEmptyListWhenNoCasesForCaseType() {
        SearchResultViewHeaderGroup header = new SearchResultViewHeaderGroup(
            new HeaderGroupMetadata(JURISDICTION, CASE_TYPE), emptyList(), emptyList()
        );
        UICaseSearchResult uiCaseSearchResult = new UICaseSearchResult(singletonList(header), emptyList(), 0L, null);

        List<SearchResultViewItem> result = uiCaseSearchResult.findCasesByCaseType(CASE_TYPE);

        assertAll(
            () -> assertThat(result.size(), is(0))
        );
    }
}
