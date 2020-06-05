package uk.gov.hmcts.ccd.domain.service.validate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ccd.data.definition.CachedCaseDefinitionRepository;
import uk.gov.hmcts.ccd.data.definition.CaseDefinitionRepository;
import uk.gov.hmcts.ccd.domain.model.definition.CaseEventFieldComplexDefinition;
import uk.gov.hmcts.ccd.domain.model.definition.CaseTypeDefinition;
import uk.gov.hmcts.ccd.domain.model.std.CaseDataContent;
import uk.gov.hmcts.ccd.domain.service.common.CaseTypeService;
import uk.gov.hmcts.ccd.domain.service.processor.FieldProcessorService;
import uk.gov.hmcts.ccd.endpoint.exceptions.ValidationException;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultValidateCaseFieldsOperation implements ValidateCaseFieldsOperation {

    private final CaseDefinitionRepository caseDefinitionRepository;
    private final CaseTypeService caseTypeService;
    private final FieldProcessorService fieldProcessorService;
    public static final String ORGANISATION_POLICY_ROLE = "OrgPolicyCaseAssignedRole";

    @Inject
    DefaultValidateCaseFieldsOperation(
        @Qualifier(CachedCaseDefinitionRepository.QUALIFIER) final CaseDefinitionRepository caseDefinitionRepository,
        final CaseTypeService caseTypeService,
        final FieldProcessorService fieldProcessorService
    ) {

        this.caseDefinitionRepository = caseDefinitionRepository;
        this.caseTypeService = caseTypeService;
        this.fieldProcessorService = fieldProcessorService;
    }

    @Override
    public final Map<String, JsonNode> validateCaseDetails(String caseTypeId, CaseDataContent content) {
        if (content == null || content.getEvent() == null || content.getEventId() == null) {
            throw new ValidationException("Cannot validate case field because of event is not specified");
        }
        final CaseTypeDefinition caseTypeDefinition = caseDefinitionRepository.getCaseType(caseTypeId);
        if (caseTypeDefinition == null) {
            throw new ValidationException("Cannot find case type definition for " + caseTypeId);
        }
        if (!hasEventId(caseTypeDefinition, content.getEventId())) {
            throw new ValidationException("Cannot validate case field because of event " + content.getEventId() + " is not found in case type definition");
        }
        content.setData(fieldProcessorService.processData(content.getData(), caseTypeDefinition, content.getEventId()));
        caseTypeService.validateData(content.getData(), caseTypeDefinition);

        validateOrganisationPolicy(caseTypeId, content);
        return content.getData();
    }

    private void validateOrganisationPolicy(String caseTypeId, CaseDataContent content) {

        // if there is not a default value. it means that there will not be organisation policy validation.
        // hence if there is at least one ORGANISATION_POLICY_ROLE all default value logic will be executed.
        if (!isOrganisationPolicyLogicInTheContent(content)) {
            return;
        }
        caseDefinitionRepository.getCaseType(caseTypeId).getEvents().stream().filter(
            event -> event.getId().equals(content.getEventId())
        ).forEach(
            caseEventDefinition -> caseEventDefinition.getCaseFields().stream().forEach(
                caseField -> caseField.getCaseEventFieldComplexDefinitions().stream().filter(
                    caseEventFieldComplexDefinition -> {
                        if (caseEventFieldComplexDefinition.getReference().equals(ORGANISATION_POLICY_ROLE)) {
                            //get extract the default value  from the content for the current caseField
                            final Optional<String> caseFieldDefaultValue = getDefaultValueFromContentByCaseFieldID(content, caseField.getCaseFieldId());
                            return validateOrgPolicyCaseAssignedRole(
                                caseEventFieldComplexDefinition,
                                caseFieldDefaultValue,
                                caseField.getCaseFieldId());
                        } else {
                            return false;
                        }
                    }
                ).collect(Collectors.toList()))
        );
    }

    private boolean isOrganisationPolicyLogicInTheContent(final CaseDataContent content) {
        final JsonNode existingData = new ObjectMapper().convertValue(content.getData(), JsonNode.class);
        final List<JsonNode> jsonNode = existingData.findParents(ORGANISATION_POLICY_ROLE);
        final Optional<JsonNode> node = jsonNode.stream().findFirst();
        return  node.isPresent();
    }

    private Optional<String> getDefaultValueFromContentByCaseFieldID(final CaseDataContent content, final String caseFiledID) {
        final JsonNode existingData = new ObjectMapper().convertValue(content.getData(), JsonNode.class);
        final Optional<JsonNode> caseFieldNode = Optional.ofNullable(existingData.get(caseFiledID));

        if (caseFieldNode.isPresent()) {
            return Optional.of(caseFieldNode.get().get(ORGANISATION_POLICY_ROLE).textValue());
        }
        return Optional.ofNullable(null);
    }

    private boolean validateOrgPolicyCaseAssignedRole(final CaseEventFieldComplexDefinition caseEventFieldComplexDefinition,
                                                      final Optional<String> defaultValue, String caseFiledID) {
        if (!defaultValue.isPresent()) {
            throw new ValidationException("The organisation policy role filed " + caseFiledID + " cannot have an empty value.");
        }

        if (!caseEventFieldComplexDefinition.getDefaultValue().equals(defaultValue.get())) {
            throw new ValidationException("The organisation policy role filed " + caseFiledID + " has an incorrect value.");
        }
        return false;
    }


    private boolean hasEventId(CaseTypeDefinition caseTypeDefinition, String eventId) {
        return caseTypeDefinition.hasEventId(eventId);
    }

    @Override
    public void validateData(Map<String, JsonNode> data, CaseTypeDefinition caseTypeDefinition) {
        caseTypeService.validateData(data, caseTypeDefinition);
    }
}
