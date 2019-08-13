package uk.gov.hmcts.ccd.data.casedetails.search;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class SortOrderQueryBuilder {

    private static final String DATA_FIELD = "data";
    private static final String CREATED_DATE = "created_date";
    private static final String SPACE = " ";
    private static final String COMMA = ",";

    public String buildSortOrderClause(MetaData metaData) {
        StringBuilder sb = new StringBuilder();
        metaData.getSortOrderFields().forEach(sortOrderField -> {
            if (sortOrderField.isMetadata()) {
                sb.append(sortOrderField.getCaseFieldId());
            } else {
                sb.append(convertFieldNameToJSONBsqlFormat(sortOrderField.getCaseFieldId()));
            }
            sb.append(SPACE);
            sb.append(sortOrderField.getDirection());
            sb.append(COMMA);
            sb.append(SPACE);
        });
        // always sort with creation_date as a last order so that it supports cases where no values at all for the configured fields and also default fallback.
        return sb.append(CREATED_DATE + SPACE + SortDirection.fromOptionalString(metaData.getSortDirection())).toString();
    }

    private static String convertFieldNameToJSONBsqlFormat(final String in) {
        return DATA_FIELD + " #>> '{" + StringUtils.replace(in, ".", ",") + "}'";
    }

}
