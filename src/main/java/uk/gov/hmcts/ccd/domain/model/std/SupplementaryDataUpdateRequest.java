package uk.gov.hmcts.ccd.domain.model.std;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uk.gov.hmcts.ccd.data.casedetails.supplementarydata.SupplementaryDataOperation;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SupplementaryDataUpdateRequest {

    private Map<String, Map<String, Object>> requestData;

    @JsonIgnore
    public Map<String, Object> getOperationProperties(SupplementaryDataOperation operation) {
        return this.requestData.getOrDefault(operation.getOperationName(), new HashMap<>());
    }

    @JsonIgnore
    public Set<String> getRequestDataKeys() {
        Set<String> keys = new HashSet<>();
        if (this.requestData != null) {
            this.requestData.keySet().forEach(key -> {
                Map<String, Object> operationData = this.requestData.get(key);
                keys.addAll(operationData.keySet());
            });
        }
        return keys;
    }

}
