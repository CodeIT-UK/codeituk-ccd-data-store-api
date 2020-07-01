package uk.gov.hmcts.ccd.domain.service.supplementarydata;

import java.util.EnumMap;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ccd.data.casedetails.supplementarydata.SupplementaryDataOperation;
import uk.gov.hmcts.ccd.data.casedetails.supplementarydata.SupplementaryDataRepository;
import uk.gov.hmcts.ccd.domain.model.std.SupplementaryData;
import uk.gov.hmcts.ccd.domain.model.std.SupplementaryDataUpdateRequest;

@Service
@Qualifier("default")
public class DefaultSupplementaryDataUpdateOperation implements SupplementaryDataUpdateOperation {

    private final SupplementaryDataRepository supplementaryDataRepository;

    private EnumMap<SupplementaryDataOperation, BiConsumer<String, SupplementaryDataUpdateRequest>> supplementaryFunctions =
        new EnumMap<>(SupplementaryDataOperation.class);

    @Autowired
    public DefaultSupplementaryDataUpdateOperation(final @Qualifier("default") SupplementaryDataRepository supplementaryDataRepository) {
        this.supplementaryDataRepository = supplementaryDataRepository;
        supplementaryFunctions.put(SupplementaryDataOperation.SET, this.supplementaryDataRepository::setSupplementaryData);
        supplementaryFunctions.put(SupplementaryDataOperation.INC, this.supplementaryDataRepository::incrementSupplementaryData);
    }

    @Override
    public SupplementaryData updateSupplementaryData(String caseReference, SupplementaryDataUpdateRequest supplementaryData) {
        supplementaryData.getRequestData().keySet().forEach(key -> {
            Optional<SupplementaryDataOperation> operation = SupplementaryDataOperation.getOperation(key);
            if (operation.isPresent()) {
                supplementaryFunctions
                    .get(operation.get())
                    .accept(caseReference, supplementaryData);
            }
        });
        return this.supplementaryDataRepository.findSupplementaryData(caseReference);
    }
}
