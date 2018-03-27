package uk.gov.hmcts.ccd.datastore.tests.helper;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import uk.gov.hmcts.ccd.domain.model.std.CaseDataContent;
import java.util.function.Supplier;

public class CCDHelper {

    public Response createCase(Supplier<RequestSpecification> asUser,
                               String jurisdiction,
                               String caseType,
                               String event,
                               CaseDataContent casePayload) {

        casePayload.getEvent().setEventId(event);
        casePayload.setToken(generateTokenCreateCase(asUser, jurisdiction, caseType, event));

        return asUser.get()
            .given()
            .pathParam("jurisdiction", jurisdiction)
            .pathParam("caseType", caseType)
            .contentType(ContentType.JSON)
            .body(casePayload)
            .when()
            .post("/caseworkers/{user}/jurisdictions/{jurisdiction}/case-types/{caseType}/cases");
    }


    public String generateTokenCreateCase(Supplier<RequestSpecification> asUser,
                                          String jurisdiction,
                                          String caseType,
                                          String event) {

        final String eventToken = asUser.get()
            .given()
            .pathParam("jurisdiction", jurisdiction)
            .pathParam("caseType", caseType)
            .pathParam("event", event)
            .contentType(ContentType.JSON)
            .when()
            .get(
                "/caseworkers/{user}/jurisdictions/{jurisdiction}/case-types/{caseType}/event-triggers/{event}/token")
            .then()
            .statusCode(200)
            .extract()
            .path("token");

        return eventToken;

    }

    public String generateTokenUpdateCase(Supplier<RequestSpecification> asUser,
                                          String jurisdiction,
                                          String caseType,
                                          String event,
                                          Long caseID) {

        final String eventToken = asUser.get()
            .given()
            .pathParam("jurisdiction", jurisdiction)
            .pathParam("caseType", caseType)
            .pathParam("event", event)
            .pathParam("caseID",caseID)
            .contentType(ContentType.JSON)
            .when()
            .get("/caseworkers/{user}/jurisdictions/{jurisdiction}/case-types/{caseType}/cases/{caseID}/event-triggers/{event}/token")
            .then()
            .statusCode(200)
            .extract()
            .path("token");

        return eventToken;

    }

}



