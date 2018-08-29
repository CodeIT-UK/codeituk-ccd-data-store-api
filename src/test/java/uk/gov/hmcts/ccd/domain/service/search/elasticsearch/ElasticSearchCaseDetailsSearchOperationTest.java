package uk.gov.hmcts.ccd.domain.service.search.elasticsearch;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.ccd.ApplicationParams;
import uk.gov.hmcts.ccd.domain.model.definition.CaseDetails;
import uk.gov.hmcts.ccd.domain.service.search.elasticsearch.dto.ElasticSearchCaseDetailsDTO;
import uk.gov.hmcts.ccd.domain.service.search.elasticsearch.mapper.CaseDetailsMapper;
import uk.gov.hmcts.ccd.endpoint.exceptions.BadSearchRequest;

@RunWith(MockitoJUnitRunner.class)
public class ElasticSearchCaseDetailsSearchOperationTest {

    private static final String INDEX_NAME_FORMAT = "%s_cases";
    private static final String CASE_TYPE_ID = "caseTypeId";
    private static final String INDEX_TYPE = "case";
    private String caseDetailsElastic = "{some case details}";

    @InjectMocks
    private ElasticSearchCaseDetailsSearchOperation searchOperation;

    @Mock
    private ApplicationParams applicationParams;

    @Mock
    private JestClient jestClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CaseDetailsMapper mapper;

    @Mock
    private ElasticSearchCaseDetailsDTO caseDetailsDTO;

    @Mock
    private CaseDetails caseDetails;

    @Before
    public void setup() {
        when(applicationParams.getCasesIndexNameFormat()).thenReturn(INDEX_NAME_FORMAT);
        when(applicationParams.getCasesIndexType()).thenReturn(INDEX_TYPE);
    }

    @Test
    public void searchShouldMapElasticSearchResultToCaseDetails() throws IOException {
        ArgumentCaptor<Search> arg = ArgumentCaptor.forClass(Search.class);
        SearchResult searchResult = mock(SearchResult.class);
        when(searchResult.isSucceeded()).thenReturn(true);
        when(searchResult.getSourceAsStringList()).thenReturn(newArrayList(caseDetailsElastic));
        when(objectMapper.readValue(caseDetailsElastic, ElasticSearchCaseDetailsDTO.class))
                .thenReturn(caseDetailsDTO);
        when(mapper.dtosToCaseDetailsList(newArrayList(caseDetailsDTO))).thenReturn(newArrayList(caseDetails));
        when(jestClient.execute(any(Search.class))).thenReturn(searchResult);

        List<CaseDetails> caseDetails = searchOperation.execute(CASE_TYPE_ID, "{query}");

        verify(jestClient).execute(arg.capture());

        Search searchRequest = arg.getValue();
        assertThat(searchRequest.getIndex(), equalTo(String.format(INDEX_NAME_FORMAT, CASE_TYPE_ID)));
        assertThat(searchRequest.getType(), equalTo(INDEX_TYPE));
        assertThat(caseDetails, equalTo(newArrayList(caseDetails)));
    }

    @Test
    public void searchShouldReturnBadSearchRequestOnFailure() throws IOException {
        SearchResult searchResult = mock(SearchResult.class);
        when(searchResult.isSucceeded()).thenReturn(false);

        when(jestClient.execute(any(Search.class))).thenReturn(searchResult);

        assertThrows(BadSearchRequest.class, () -> searchOperation.execute(CASE_TYPE_ID, "{query}"));
    }
}