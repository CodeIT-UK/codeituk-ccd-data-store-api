package uk.gov.hmcts.ccd.data.casedetails.supplementarydata;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.WireMockBaseTest;
import uk.gov.hmcts.ccd.domain.model.std.SupplementaryDataUpdateRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SetSupplementaryDataQueryBuilderTest extends WireMockBaseTest {

    private static final String CASE_REFERENCE = "1234569";

    @PersistenceContext
    private EntityManager em;

    @Inject
    private SetSupplementaryDataQueryBuilder supplementaryDataQueryBuilder;

    @Test
    void shouldReturnEmptyQueryList() {
        List<Query> queryList = supplementaryDataQueryBuilder.buildQueryForEachSupplementaryDataProperty(em, CASE_REFERENCE,
            new SupplementaryDataUpdateRequest(new HashMap<>()));
        assertNotNull(queryList);
        assertEquals(0, queryList.size());
    }

    @Test
    void shouldReturnQueryListWhenRequestDataPassed() {
        Map<String, Map<String, Object>> requestData = createRequestData();
        List<Query> queryList = supplementaryDataQueryBuilder.buildQueryForEachSupplementaryDataProperty(em, CASE_REFERENCE,
            new SupplementaryDataUpdateRequest(requestData));
        assertNotNull(queryList);
        assertEquals(1, queryList.size());
        assertEquals(CASE_REFERENCE, queryList.get(0).getParameterValue("reference"));
        assertEquals("{orgs_assigned_users,organisationA}", queryList.get(0).getParameterValue("leaf_node_key"));
        assertEquals(32, queryList.get(0).getParameterValue("value"));
        assertEquals("{orgs_assigned_users}", queryList.get(0).getParameterValue("parent_key"));
    }

    @Test
    void shouldReturnMoreThanOneQueryInTheListWhenRequestDataPassedWithMultipleValues() {
        Map<String, Map<String, Object>> requestData = createRequestDataMultiple();
        List<Query> queryList = supplementaryDataQueryBuilder.buildQueryForEachSupplementaryDataProperty(em, CASE_REFERENCE,
            new SupplementaryDataUpdateRequest(requestData));
        assertNotNull(queryList);
        assertEquals(2, queryList.size());
        assertEquals(CASE_REFERENCE, queryList.get(0).getParameterValue("reference"));
    }

    private Map<String, Map<String, Object>> createRequestData() {
        String jsonRequest = "{\n"
            + "\t\"$set\": {\n"
            + "\t\t\"orgs_assigned_users\": {\n"
            + "\t\t\"organisationA\": 32\n"
            + "\t\t}\n"
            + "\t}\n"
            + "}";

        return convertData(jsonRequest);
    }

    private Map<String, Map<String, Object>>  createRequestDataMultiple() {
        String jsonRequest = "{\n"
            + "\t\"$set\": {\n"
            + "\t\t\"orgs_assigned_users\": {\n"
            + "\t\t\"organisationA\": 32,\n"
            + "\t\t\"organisationB\": 36\n"
            + "\t\t}\n"
            + "\t}\n"
            + "}";

        return convertData(jsonRequest);
    }

    private Map<String, Map<String, Object>> convertData(String jsonRquest) {
        Map<String, Map<String, Object>> requestData;
        try {
            requestData = mapper.readValue(jsonRquest, Map.class);
        } catch (JsonProcessingException e) {
            requestData = new HashMap<>();
        }
        return requestData;
    }
}
