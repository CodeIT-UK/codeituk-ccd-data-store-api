package uk.gov.hmcts.ccd.domain.model.aggregated;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldType;
import uk.gov.hmcts.ccd.domain.model.definition.FixedListItem;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static uk.gov.hmcts.ccd.domain.model.definition.FieldType.COLLECTION;
import static uk.gov.hmcts.ccd.domain.model.definition.FieldType.COMPLEX;
import static uk.gov.hmcts.ccd.domain.model.definition.FieldType.MULTI_SELECT_LIST;
import static uk.gov.hmcts.ccd.domain.service.common.TestBuildersUtil.CaseFieldBuilder.newCaseField;
import static uk.gov.hmcts.ccd.domain.service.common.TestBuildersUtil.FieldTypeBuilder.aFieldType;
import static uk.gov.hmcts.ccd.domain.service.common.TestBuildersUtil.FixedListItemBuilder.aFixedListItem;

class CompoundFieldValueServiceTest {

    private static final String TEXT_TYPE = "Text";
    private final ObjectMapper MAPPER = new ObjectMapper();
    final TypeReference STRING_JSON_MAP = new TypeReference<HashMap<String, JsonNode>>() {
    };

    @Test
    @DisplayName("should sort compound data")
    void shouldSortMultiSelectListData() throws IOException {
        FieldType multiSelectListTypes = aFieldType()
            .withType(MULTI_SELECT_LIST)
            .withFixedListItems(fixedListItem("item2", 1),
                                fixedListItem("item3", 2),
                                fixedListItem("item1", 3))
            .build();
        CaseField caseField = newCaseField()
            .withId("MultiSelect")
            .withFieldType(multiSelectListTypes)
            .build();

        final Map<String, JsonNode> data = MAPPER.convertValue(MAPPER.readTree("{ \"MultiSelect\": [\"item1\",\"item2\",\"item3\"] }"), STRING_JSON_MAP);
        JsonNode result = CompoundFieldValueService.getSortedValue(caseField, data.get("MultiSelect"));

        final String expected = "[\"item2\",\"item3\",\"item1\"]";
        String actualResult = MAPPER.writeValueAsString(result);
        Assert.assertThat(actualResult, equalTo(expected));
    }

    @Test
    @DisplayName("should sort compound data")
    void shouldSortData() throws IOException {
        FieldType complexOfSimpleTypes = aFieldType()
            .withType(COMPLEX)
            .withComplexField(simpleField("simple1", 1))
            .withComplexField(simpleField("simple3", 2))
            .withComplexField(simpleField("simple2", 3))
            .withComplexField(simpleMultiSelectListField("simple5", 4,
                                                         fixedListItem("item2", 1),
                                                         fixedListItem("item3", 2),
                                                         fixedListItem("item1", 3)))
            .withComplexField(simpleField("simple4", 5))
            .build();
        FieldType multipleNestedCompoundFieldType = aFieldType()
            .withType(COMPLEX)
            .withComplexField(newCaseField()
                                  .withId("complex1")
                                  .withFieldType(aFieldType()
                                                     .withType(COLLECTION)
                                                     .withCollectionFieldType(aFieldType()
                                                                                  .withType(COMPLEX)
                                                                                  .withComplexField(newCaseField()
                                                                                                        .withId("complex2")
                                                                                                        .withFieldType(complexOfSimpleTypes)
                                                                                                        .withOrder(1)
                                                                                                        .build())
                                                                                  .build())
                                                     .build())
                                  .withOrder(1)
                                  .build())
            .withComplexField(newCaseField()
                                  .withId("complex3")
                                  .withFieldType(aFieldType()
                                                     .withType(COMPLEX)
                                                     .withComplexField(simpleField("simple9", 1))
                                                     .withComplexField(simpleField("simple8", 2))
                                                     .withComplexField(simpleField("simple6", 3))
                                                     .withComplexField(simpleField("simple10", 4))
                                                     .withComplexField(simpleField("simple7", 5))
                                                     .build())
                                  .withOrder(2)
                                  .build())
            .build();


        CaseField caseField = newCaseField()
            .withId("Compound")
            .withFieldType(aFieldType()
                               .withType(COLLECTION)
                               .withCollectionFieldType(multipleNestedCompoundFieldType)
                               .build())
            .build();

        final Map<String, JsonNode> data = MAPPER.convertValue(MAPPER.readTree(
            "{  \"Compound\": [\n" +
                "       {\n" +
                "         \"id\": \"id1\",\n" +
                "         \"value\": {\n" +
                "            \"complex3\":{\n" +
                "               \"simple6\": \"value6\",\n" +
                "               \"simple7\": \"value7\",\n" +
                "               \"simple8\": \"value8\",\n" +
                "               \"simple9\": \"value9\",\n" +
                "               \"simple10\": \"value10\"\n" +
                "            },\n" +
                "            \"complex1\": [\n" +
                "               {\n" +
                "                  \"id\": \"id2\",\n" +
                "                  \"value\" : {\n" +
                "                     \"complex2\": {\n" +
                "                        \"simple1\": \"value1\",\n" +
                "                        \"simple2\": \"value2\",\n" +
                "                        \"simple3\": \"value3\",\n" +
                "                        \"simple4\": \"value4\",\n" +
                "                        \"simple5\": [\"item1\",\"item2\",\"item3\"]\n" +
                "                     }\n" +
                "                  }\n" +
                "               }\n" +
                "            ]\n" +
                "         }\n" +
                "       }\n" +
                "   ]\n" +
                "}"
        ), STRING_JSON_MAP);
        JsonNode result = CompoundFieldValueService.getSortedValue(caseField, data.get("Compound"));

        final String expected = "[{\"id\":\"id1\",\"value\":{\"complex1\":[{\"id\":\"id2\",\"value\":{\"complex2\":{\"simple1\":\"value1\",\"simple3\":\"value3\",\"simple2\":\"value2\",\"simple5\":[\"item2\",\"item3\",\"item1\"],\"simple4\":\"value4\"}}}],\"complex3\":{\"simple9\":\"value9\",\"simple8\":\"value8\",\"simple6\":\"value6\",\"simple10\":\"value10\",\"simple7\":\"value7\"}}}]";
        String actualResult = MAPPER.writeValueAsString(result);
        Assert.assertThat(actualResult, equalTo(expected));
    }

    private FixedListItem fixedListItem(final String value, final int order) {
        return aFixedListItem().withCode(value).withOrder(String.valueOf(order)).build();
    }

    private FieldType simpleType() {
        return aFieldType().withType(TEXT_TYPE).build();
    }

    private FieldType multiSelectListType(final List<FixedListItem> fixedListItems) {
        return aFieldType().withType(MULTI_SELECT_LIST).withFixedListItems(fixedListItems).build();
    }

    private CaseField simpleField(final String id, final Integer order) {
        return newCaseField()
            .withId(id)
            .withFieldType(simpleType())
            .withOrder(order)
            .build();
    }

    private CaseField simpleMultiSelectListField(final String id, final Integer order, FixedListItem... fixedListItems) {
        return newCaseField()
            .withId(id)
            .withFieldType(multiSelectListType(Lists.newArrayList(fixedListItems)))
            .withOrder(order)
            .build();
    }

}
