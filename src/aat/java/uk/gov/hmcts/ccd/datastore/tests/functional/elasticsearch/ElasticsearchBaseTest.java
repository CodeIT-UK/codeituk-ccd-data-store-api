package uk.gov.hmcts.ccd.datastore.tests.functional.elasticsearch;

import java.io.File;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static uk.gov.hmcts.ccd.datastore.tests.fixture.AATCaseType.AAT_PRIVATE_CASE_TYPE;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import uk.gov.hmcts.ccd.datastore.tests.AATHelper;
import uk.gov.hmcts.ccd.datastore.tests.BaseTest;
import uk.gov.hmcts.ccd.datastore.tests.fixture.AATCaseBuilder;
import uk.gov.hmcts.ccd.datastore.tests.fixture.AATCaseType;

abstract class ElasticsearchBaseTest extends BaseTest {

    private static final String DEFINITION_FILE = "src/aat/resources/CCD_CNP_27.xlsx";
    private static final String CASE_INDEX_NAME = "aat_private_cases-000001";
    private static final String CASE_INDEX_ALIAS = "aat_private_cases";

    ElasticsearchBaseTest(AATHelper aat) {
        super(aat);
    }

    void assertElasticsearchEnabled() {
        // stop execution of these tests if Elasticsearch is not enabled
        boolean elasticsearchEnabled = ofNullable(System.getenv("ELASTIC_SEARCH_ENABLED")).map(Boolean::valueOf).orElse(false);
        assumeTrue(elasticsearchEnabled, () -> "Ignoring Elasticsearch tests, variable ELASTIC_SEARCH_ENABLED not set");
    }

    void importDefinition() {
        asAutoTestImporter()
            .given()
            .multiPart(new File(DEFINITION_FILE))
            .expect()
            .statusCode(201)
            .when()
            .post("/import");
    }

    ValidatableResponse searchCaseAsPrivateCaseWorker(String jsonSearchRequest) {
        return searchCase(asPrivateCaseworker(false), jsonSearchRequest);
    }

    ValidatableResponse searchCase(Supplier<RequestSpecification> requestSpecification, String jsonSearchRequest) {
        return requestSpecification.get()
            .given()
            .log()
            .body()
            .queryParam("ctid", AAT_PRIVATE_CASE_TYPE)
            .contentType(ContentType.JSON)
            .body(jsonSearchRequest)
            .when()
            .post("/searchCases")
            .then()
            .statusCode(200);
    }

    void deleteIndexAndAlias() {
        deleteIndexAlias(CASE_INDEX_NAME, CASE_INDEX_ALIAS);
        deleteIndex(CASE_INDEX_NAME);
    }

    private void deleteIndexAlias(String indexName, String indexAlias) {
        asElasticsearchApiUser()
            .when()
            .delete(indexName + "/_alias/" + indexAlias)
            .then()
            .statusCode(200)
            .body("acknowledged", equalTo(true));
    }

    private void deleteIndex(String indexName) {
        asElasticsearchApiUser()
            .when()
            .delete(indexName)
            .then()
            .statusCode(200)
            .body("acknowledged", equalTo(true));
    }

    private RequestSpecification asElasticsearchApiUser() {
        return RestAssured.given(new RequestSpecBuilder()
                                     .setBaseUri(aat.getElasticsearchBaseUri())
                                     .build());
    }

    Long createCaseAndProgressState(Supplier<RequestSpecification> asUser) {
        Long caseReference = createCase(asUser, AATCaseBuilder.EmptyCase.build());
        AATCaseType.Event.startProgress(AAT_PRIVATE_CASE_TYPE, caseReference)
            .as(asUser)
            .submit()
            .then()
            .statusCode(201)
            .assertThat()
            .body("state", Matchers.equalTo(AATCaseType.State.IN_PROGRESS));

        return caseReference;
    }

    Long createCase(Supplier<RequestSpecification> asUser, AATCaseType.CaseData caseData) {
        return AATCaseType.Event.create(AAT_PRIVATE_CASE_TYPE)
            .as(asUser)
            .withData(caseData)
            .submitAndGetReference();
    }

    void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
