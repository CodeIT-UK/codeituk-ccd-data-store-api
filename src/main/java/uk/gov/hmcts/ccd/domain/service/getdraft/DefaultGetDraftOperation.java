package uk.gov.hmcts.ccd.domain.service.getdraft;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ccd.data.draft.DefaultDraftGateway;
import uk.gov.hmcts.ccd.data.draft.DraftGateway;
import uk.gov.hmcts.ccd.domain.model.draft.DraftResponse;

import javax.inject.Inject;
import java.util.Optional;

@Service
@Qualifier(DefaultGetDraftOperation.QUALIFIER)
public class DefaultGetDraftOperation implements GetDraftOperation {
    public static final String QUALIFIER = "default";
    private final DraftGateway draftGateway;

    @Inject
    public DefaultGetDraftOperation(@Qualifier(DefaultDraftGateway.QUALIFIER) final DraftGateway draftGateway) {
        this.draftGateway = draftGateway;
    }

    @Override
    public Optional<DraftResponse> execute(final String draftId) {
        DraftResponse draftResponse = draftGateway.get(draftId);
        return draftResponse == null ? Optional.empty() : Optional.of(draftResponse);
    }

}
