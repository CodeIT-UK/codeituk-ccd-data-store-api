package uk.gov.hmcts.ccd.domain.model.search;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.domain.model.aggregated.CommonField;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldType;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static uk.gov.hmcts.ccd.domain.service.common.TestBuildersUtil.CaseFieldBuilder.newCaseField;
import static uk.gov.hmcts.ccd.domain.service.common.TestBuildersUtil.FieldTypeBuilder.aFieldType;

public class FieldTest {

    private static final String FIELD_ID = "Field";
    private static final String COMPLEX_FIELD_TYPE = "Complex";
    private static final String COLLECTION_FIELD_TYPE = "Collection";

    private static final FieldType TEXT_FIELD_TYPE = aFieldType().withType("Text").build();

    @Test
    void shouldFindBasicNestedField() {
        String testPath = "Field.NestedField";
        CaseField nestedField = newCaseField()
            .withId("NestedField")
            .withFieldType(aFieldType().withType("Text").build())
            .build();
        FieldType topLevelFieldType = aFieldType().withType(COMPLEX_FIELD_TYPE).withComplexField(nestedField).build();
        Field field = field(topLevelFieldType);

        final Optional<CommonField> result = field.getNestedField(testPath);

        assertAll(
            () -> assertThat(result.isPresent(), is(true)),
            () -> assertThat(result.get(), is(nestedField))
        );
    }

    @Test
    void shouldFindDeepNestedField() {
        String testPath = "Field.NestedField.DeepNestedField";
        CaseField deepNestedField = newCaseField().withId("DeepNestedField").build();
        CaseField nestedField = newCaseField()
            .withId("NestedField")
            .withFieldType(aFieldType().withType(COMPLEX_FIELD_TYPE)
                .withComplexField(newCaseField().withId("SomeOtherField").build())
                .withComplexField(deepNestedField)
                .build()
            ).build();
        FieldType topLevelFieldType = aFieldType().withType(COMPLEX_FIELD_TYPE).withComplexField(nestedField).build();
        Field field = field(topLevelFieldType);

        final Optional<CommonField> result = field.getNestedField(testPath);

        assertAll(
            () -> assertThat(result.isPresent(), is(true)),
            () -> assertThat(result.get(), is(deepNestedField))
        );
    }

    @Test
    void shouldFindNestedCollectionField() {
        String testPath = "Field.NestedCollectionField";
        CaseField collectionField = newCaseField()
            .withId("NestedCollectionField")
            .withFieldType(aFieldType().withType(COLLECTION_FIELD_TYPE)
                .withCollectionField(newCaseField().withId("SomeOtherField").build())
                .build()
            ).build();
        FieldType topLevelFieldType = aFieldType().withType(COLLECTION_FIELD_TYPE).withCollectionField(collectionField).build();
        Field field = field(topLevelFieldType);

        final Optional<CommonField> result = field.getNestedField(testPath);

        assertAll(
            () -> assertThat(result.isPresent(), is(true)),
            () -> assertThat(result.get(), is(collectionField))
        );
    }

    @Test
    void shouldNotReturnResultForNonExistentNestedField() {
        String testPath = "Field.NestedField.NonExistentDeepNestedField";
        CaseField deepNestedField = newCaseField().withId("DeepNestedField").build();
        CaseField nestedField = newCaseField()
            .withId("NestedField")
            .withFieldType(aFieldType().withType("Complex")
                .withComplexField(newCaseField().withId("SomeOtherField").build())
                .withComplexField(deepNestedField)
                .build()
            ).build();
        FieldType topLevelFieldType = aFieldType().withType(COMPLEX_FIELD_TYPE).withComplexField(nestedField).build();
        Field field = field(topLevelFieldType);

        final Optional<CommonField> result = field.getNestedField(testPath);

        assertAll(
            () -> assertThat(result.isPresent(), is(false))
        );
    }

    @Test
    void shouldNotReturnResultForBlankPath() {
        String testPath = "";
        Field field = field(TEXT_FIELD_TYPE);

        final Optional<CommonField> result = field.getNestedField(testPath);

        assertAll(() -> {
            assertThat(result.isPresent(), is(false));
        });
    }

    @Test
    void shouldNotReturnResultForPathWithNoNesting() {
        String testPath = "NonNestedPath";
        Field field = field(TEXT_FIELD_TYPE);

        final Optional<CommonField> result = field.getNestedField(testPath);

        assertAll(() -> {
            assertThat(result.isPresent(), is(false));
        });
    }

    @Test
    void shouldNotReturnResultForFieldTypeWithNoChildren() {
        String testPath = "Field.ID";
        Field field = field(TEXT_FIELD_TYPE);

        final Optional<CommonField> result = field.getNestedField(testPath);

        assertAll(() -> {
            assertThat(result.isPresent(), is(false));
        });
    }

    private Field field(FieldType fieldType) {
        Field field = new Field();
        field.setId(FIELD_ID);
        field.setType(fieldType);
        return field;
    }
}
