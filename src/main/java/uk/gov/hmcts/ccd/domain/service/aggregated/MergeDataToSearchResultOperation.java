package uk.gov.hmcts.ccd.domain.service.aggregated;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.ccd.data.definition.UIDefinitionRepository;
import uk.gov.hmcts.ccd.domain.model.definition.CaseDetails;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.domain.model.definition.CaseType;
import uk.gov.hmcts.ccd.domain.model.definition.FieldType;
import uk.gov.hmcts.ccd.domain.model.definition.SearchResult;
import uk.gov.hmcts.ccd.domain.model.definition.SearchResultField;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultView;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultViewColumn;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultViewItem;
import uk.gov.hmcts.ccd.endpoint.exceptions.BadRequestException;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static java.lang.String.format;
import static uk.gov.hmcts.ccd.domain.model.definition.FieldType.LABEL;

@Named
@Singleton
public class MergeDataToSearchResultOperation {
    protected static final String WORKBASKET_VIEW = "WORKBASKET";
    private static final String NESTED_ELEMENT_NOT_FOUND_FOR_PATH = "Nested element not found for path %s";

    private final UIDefinitionRepository uiDefinitionRepository;

    public MergeDataToSearchResultOperation(final UIDefinitionRepository uiDefinitionRepository) {
        this.uiDefinitionRepository = uiDefinitionRepository;
    }

    public SearchResultView execute(final CaseType caseType,
                                    final List<CaseDetails> caseDetails,
                                    final String view,
                                    final String resultError) {
        final SearchResult searchResult = getSearchResultDefinitions(caseType, view);

        final List<SearchResultViewColumn> viewColumns = buildSearchResultViewColumn(caseType, searchResult);

        final List<SearchResultViewItem> viewItems = caseDetails.stream()
            .map(caseData -> buildSearchResultViewItem(caseData, caseType, searchResult))
            .collect(Collectors.toList());

        return new SearchResultView(viewColumns, viewItems, resultError);
    }

    private List<SearchResultViewColumn> buildSearchResultViewColumn(CaseType caseType,
                                                                     SearchResult searchResult) {
        return Arrays.stream(searchResult.getFields())
            .flatMap(searchResultField -> caseType.getCaseFields().stream()
                    .filter(caseField -> caseField.getId().equals(searchResultField.getCaseFieldId()))
                    .map(caseField -> new SearchResultViewColumn(
                        buildCaseFieldId(searchResultField),
                        buildCaseFieldType(searchResultField, caseField),
                        searchResultField.getLabel(),
                        searchResultField.getDisplayOrder(),
                        searchResultField.isMetadata()))
                    )
            .collect(Collectors.toList());
    }

    private String buildCaseFieldId(SearchResultField searchResultField) {
        if (StringUtils.isNotBlank(searchResultField.getCaseFieldPath())) {
            return searchResultField.getCaseFieldId() + '.' + searchResultField.getCaseFieldPath();
        } else {
            return searchResultField.getCaseFieldId();
        }
    }

    private FieldType buildCaseFieldType(SearchResultField searchResultField, CaseField caseField) {
        return caseField.getComplexFieldNestedField(searchResultField.getCaseFieldPath()).getFieldType();
    }

    private SearchResultViewItem buildSearchResultViewItem(final CaseDetails caseDetails,
                                                           final CaseType caseType,
                                                           final SearchResult searchResult) {

        Map<String, JsonNode> caseData = new HashMap<>(caseDetails.getData());
        Map<String, Object> caseMetadata = new HashMap<>(caseDetails.getMetadata());
        Map<String, TextNode> labels = getLabelsFromCaseFields(caseType);
        Map<String, Object> caseFields = prepareData(searchResult, caseData, caseMetadata, labels);

        String caseId = caseDetails.hasCaseReference() ? caseDetails.getReferenceAsString() : caseDetails.getId();
        return new SearchResultViewItem(caseId, caseFields);
    }

    private Map<String, Object> prepareData(SearchResult searchResult,
                                            Map<String, JsonNode> caseData,
                                            Map<String, Object> metadata,
                                            Map<String, TextNode> labels) {

        Map<String, Object> newResults = new HashMap<>();

        searchResult.getFieldsWithPaths().forEach(searchResultField -> {
            JsonNode jsonNode = caseData.get(searchResultField.getCaseFieldId());
            if (jsonNode != null) {
                newResults.put(searchResultField.getCaseFieldId() + "." + searchResultField.getCaseFieldPath(),
                    getObjectByPath(searchResultField, jsonNode));
            }
        });

        newResults.putAll(caseData);
        newResults.putAll(labels);
        newResults.putAll(metadata);

        return newResults;
    }

    private Object getObjectByPath(SearchResultField searchResultField, JsonNode value) {

        List<String> pathElements = searchResultField.getPathElements();

        return reduce(value, pathElements, searchResultField.getCaseFieldPath());
    }

    private Object reduce(JsonNode caseFields, List<String> pathElements, String path) {
        String firstPathElement = pathElements.get(0);

        JsonNode caseField = Optional.ofNullable(caseFields.get(firstPathElement))
            .orElseThrow(() -> new BadRequestException(format(NESTED_ELEMENT_NOT_FOUND_FOR_PATH, path)));

        if (pathElements.size() == 1) {
            return caseField;
        } else {
            List<String> tail = pathElements.subList(1, pathElements.size());
            return reduce(caseField, tail, path);
        }
    }

    private Map<String, TextNode> getLabelsFromCaseFields(CaseType caseType) {
        return caseType.getCaseFields()
            .stream()
            .filter(caseField -> LABEL.equals(caseField.getFieldType().getType()))
            .collect(Collectors.toMap(CaseField::getId, caseField -> instance.textNode(caseField.getLabel())));
    }

    private SearchResult getSearchResultDefinitions(final CaseType caseType, final String view) {
        if (WORKBASKET_VIEW.equalsIgnoreCase(view)) {
            return uiDefinitionRepository.getWorkBasketResult(caseType.getId());
        } else {
            return uiDefinitionRepository.getSearchResult(caseType.getId());
        }
    }
}
