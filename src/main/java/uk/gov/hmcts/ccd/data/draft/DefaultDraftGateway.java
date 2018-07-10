package uk.gov.hmcts.ccd.data.draft;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.ccd.ApplicationParams;
import uk.gov.hmcts.ccd.data.SecurityUtils;
import uk.gov.hmcts.ccd.domain.model.draft.*;
import uk.gov.hmcts.ccd.endpoint.exceptions.ResourceNotFoundException;
import uk.gov.hmcts.ccd.endpoint.exceptions.ServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.ccd.domain.model.draft.DraftResponseBuilder.aDraftResponse;


@Named
@Qualifier(DefaultDraftGateway.QUALIFIER)
@Singleton
public class DefaultDraftGateway implements DraftGateway {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDraftGateway.class);
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    public static final String QUALIFIER = "default";
    private static final String DRAFT_ENCRYPTION_KEY_HEADER = "Secret";
    private static final int RESOURCE_NOT_FOUND = 404;

    private final RestTemplate restTemplate;
    private final SecurityUtils securityUtils;
    private final ApplicationParams applicationParams;

    @Inject
    public DefaultDraftGateway(
            final RestTemplate restTemplate,
            final SecurityUtils securityUtils,
            final ApplicationParams applicationParams) {
        this.restTemplate = restTemplate;
        this.securityUtils = securityUtils;
        this.applicationParams = applicationParams;
    }

    @Override
    public Long save(final CreateCaseDraft draft) {
        try {
            HttpHeaders headers = securityUtils.authorizationHeaders();
            headers.add(DRAFT_ENCRYPTION_KEY_HEADER, applicationParams.getDraftEncryptionKey());
            final HttpEntity requestEntity = new HttpEntity(draft, headers);
            HttpHeaders responseHeaders = restTemplate.exchange(applicationParams.draftBaseURL(), HttpMethod.POST, requestEntity, HttpEntity.class).getHeaders();
            return getDraftId(responseHeaders);
        } catch (Exception e) {
            LOG.warn("Error while saving draft=" + draft, e);
            throw new ServiceException("Problem saving draft because of " + e.getMessage());
        }
    }

    @Override
    public DraftResponse update(final UpdateCaseDraft draft, final String draftId) {
        try {
            HttpHeaders headers = securityUtils.authorizationHeaders();
            headers.add(DRAFT_ENCRYPTION_KEY_HEADER, applicationParams.getDraftEncryptionKey());
            final HttpEntity requestEntity = new HttpEntity(draft, headers);
            restTemplate.exchange(applicationParams.draftURL(draftId), HttpMethod.PUT, requestEntity, HttpEntity.class);
            return aDraftResponse()
                .withId(draftId)
                .build();
        } catch (HttpClientErrorException e) {
            LOG.warn("Error while updating draftId=" + draftId, e);
            if (e.getRawStatusCode() == RESOURCE_NOT_FOUND) {
                throw new ResourceNotFoundException("Resource not found when getting draft for draftId=" + draftId + " because of " + e.getMessage());
            }
        } catch (Exception e) {
            LOG.warn("Error while updating draftId=" + draftId, e);
            throw new ServiceException("Problem updating draft because of " + e.getMessage());
        }
        return null;
    }

    @Override
    public DraftResponse get(final String draftId) {
        try {
            HttpHeaders headers = securityUtils.authorizationHeaders();
            headers.add(DRAFT_ENCRYPTION_KEY_HEADER, applicationParams.getDraftEncryptionKey());
            final HttpEntity requestEntity = new HttpEntity(headers);
            Draft draft = restTemplate.exchange(applicationParams.draftURL(draftId), HttpMethod.GET, requestEntity, Draft.class).getBody();
            return assembleDraft(draft);
        } catch (HttpClientErrorException e) {
            LOG.warn("Error while getting draftId=" + draftId, e);
            if (e.getRawStatusCode() == RESOURCE_NOT_FOUND) {
                throw new ResourceNotFoundException("Resource not found when getting draft for draftId=" + draftId + " because of " + e.getMessage());
            }
        } catch (Exception e) {
            LOG.warn("Error while getting draftId=" + draftId, e);
            throw new ServiceException("Problem getting draft because of " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<DraftResponse> getAll() {
        try {
            HttpHeaders headers = securityUtils.authorizationHeaders();
            headers.add(DRAFT_ENCRYPTION_KEY_HEADER, applicationParams.getDraftEncryptionKey());
            final HttpEntity requestEntity = new HttpEntity(headers);
            DraftList getDrafts = restTemplate.exchange(applicationParams.draftBaseURL(), HttpMethod.GET, requestEntity, DraftList.class).getBody();
            return getDrafts.getData()
                .stream()
                .map(d -> assembleDraft(d))
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.warn("Error while getting drafts", e);
            throw new ServiceException("Problem getting drafts because of " + e.getMessage());
        }
    }

    private DraftResponse assembleDraft(Draft getDraft) {
        DraftResponse draftResponse = null;
        try {
            draftResponse = aDraftResponse()
                .withId(getDraft.getId())
                .withDocument(MAPPER.treeToValue(getDraft.getDocument(), CaseDraft.class))
                .withType(getDraft.getType())
                .withCreated(getDraft.getCreated())
                .withUpdated(getDraft.getUpdated())
                .build();
        } catch (IOException e) {
            LOG.warn("Error while deserializing case data content", e);
            throw new ServiceException("Problem deserializing case data content because of " + e.getMessage());
        }
        return draftResponse;
    }

    private Long getDraftId(HttpHeaders responseHeaders) {
        String path = responseHeaders.getLocation().getPath();
        return Long.valueOf(path.substring(path.lastIndexOf("/") + 1));
    }
}
