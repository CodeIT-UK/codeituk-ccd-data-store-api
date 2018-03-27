package uk.gov.hmcts.ccd.data.definition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import uk.gov.hmcts.ccd.domain.model.definition.CaseType;
import uk.gov.hmcts.ccd.domain.model.definition.FieldType;
import uk.gov.hmcts.ccd.domain.model.definition.UserRole;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@Service
@Qualifier(CachedCaseDefinitionRepository.QUALIFIER)
@RequestScope
// TODO: Make this repository return copies of the maps https://tools.hmcts.net/jira/browse/RDM-1459
public class CachedCaseDefinitionRepository implements CaseDefinitionRepository {

    public static final String QUALIFIER = "cached";
    private static final Logger LOG = LoggerFactory.getLogger(CachedCaseDefinitionRepository.class);

    private final CaseDefinitionRepository caseDefinitionRepository;
    private final Map<String, List<CaseType>> caseTypesForJurisdictions = newHashMap();
    private final Map<String , CaseTypeVersionInformation> versions = newHashMap();
    private final Map<String, UserRole> userRoleClassifications = newHashMap();
    private final Map<String, List<FieldType>> baseTypes = newHashMap();

    @Autowired
    public CachedCaseDefinitionRepository(@Qualifier(DefaultCaseDefinitionRepository.QUALIFIER) CaseDefinitionRepository caseDefinitionRepository) {
        this.caseDefinitionRepository = caseDefinitionRepository;
    }

    public List<CaseType> getCaseTypesForJurisdiction(final String jurisdictionId) {
        return caseTypesForJurisdictions.computeIfAbsent(jurisdictionId, caseDefinitionRepository::getCaseTypesForJurisdiction);
    }

    public CaseType getCaseType(final String caseTypeId) {
        CaseTypeVersionInformation latestVersion = this.getLatestVersion(caseTypeId);
        return caseDefinitionRepository.getCaseType(latestVersion.getVersion(), caseTypeId);
    }

    public UserRole getUserRoleClassifications(String userRole) {
        return userRoleClassifications.computeIfAbsent(userRole, caseDefinitionRepository::getUserRoleClassifications);
    }

    @Override
    public CaseTypeVersionInformation getLatestVersion(String caseTypeId) {
        return versions.computeIfAbsent(caseTypeId, caseDefinitionRepository::getLatestVersion);
    }

    @Override
    public CaseType getCaseType(int latestVersion, String caseTypeId) {
        return caseDefinitionRepository.getCaseType(latestVersion, caseTypeId);
    }

    public List<FieldType> getBaseTypes() {
        return baseTypes.computeIfAbsent("baseTypes", e -> caseDefinitionRepository.getBaseTypes());
    }

}
