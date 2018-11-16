package uk.gov.hmcts.ccd.v2.external.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.ccd.domain.model.callbacks.StartEventTrigger;
import uk.gov.hmcts.ccd.domain.service.startevent.StartEventOperation;
import uk.gov.hmcts.ccd.v2.V2;
import uk.gov.hmcts.ccd.v2.external.resource.StartTriggerResource;

@RestController
@RequestMapping(path = "/")
public class StartTriggerController {
    private static final String ERROR_CASE_TYPE_OR_TRIGGER_ID_INVALID = "Case type or Trigger ID is not valid";

    private final StartEventOperation startEventOperation;

    @Autowired
    public StartTriggerController(
        @Qualifier("authorised") final StartEventOperation startEventOperation) {
        this.startEventOperation = startEventOperation;
    }

    @GetMapping(
        path = "/case-types/{caseTypeId}/triggers/{triggerId}",
        headers = {
            V2.EXPERIMENTAL_HEADER
        },
        produces = {
            V2.MediaType.START_TRIGGER
        }
    )
    @ApiOperation(
        value = "Retrieve a trigger by ID",
        notes = V2.EXPERIMENTAL_WARNING
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "Success",
            response = StartTriggerResource.class
        ),
        @ApiResponse(
            code = 400,
            message = ERROR_CASE_TYPE_OR_TRIGGER_ID_INVALID
        ),
        @ApiResponse(
            code = 404,
            message = "Trigger not found"
        )
    })
    public ResponseEntity<StartTriggerResource> getStartTrigger(@PathVariable("caseTypeId") String caseTypeId,
                                                                @PathVariable("triggerId") String triggerId,
                                                                @RequestParam(value = "ignore-warning", required = false) final Boolean ignoreWarning) {

        final StartEventTrigger startEventOperation = this.startEventOperation.triggerStartForCaseType(caseTypeId,
                                                                                                       triggerId,
                                                                                                       ignoreWarning);

        return ResponseEntity.ok(new StartTriggerResource(startEventOperation, ignoreWarning));
    }

}
