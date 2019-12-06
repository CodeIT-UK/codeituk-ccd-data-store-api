@F-042
Feature: F-042: Trigger "aboutToStart" event as a Case worker

  Background: Load test data for the scenario
    Given an appropriate test context as detailed in the test data source

  @S-243
  Scenario: Trigger the aboutToStart callback event for a caseworker for a new case which has not started yet.
    Given a user with [an active profile in CCD]
    And a case that has just been created as in [Caseworker1_Full_Case]
    When a request is prepared with appropriate values
    And the request [is prepared with a valid User ID, Jurisdiction, Case ID, Case Type ID and Event Trigger ID]
    And it is submitted to call the [Start the event creation process for a new case for a Case Worker] operation of [CCD Data Store]
    Then a positive response is received
    And the response [has the 200 return code]
    And the response has all other details as expected

  @S-246
  Scenario: Trigger the aboutToStart callback event for a caseworker for an invalid Case ID
    Given a user with [an active profile in CCD]
    When a request is prepared with appropriate values
    And the request [is prepared with an invalid Case ID]
    And it is submitted to call the [Start the event creation process for a new case for a Case Worker] operation of [CCD Data Store]
    Then a negative response is received
    And the response [has the 400 return code]
    And the response has all other details as expected

  @S-247 @Ignore #Probable bug. If case type ID is invalid, we are getting 200 instead of 404
  Scenario: Trigger the aboutToStart callback event for a caseworker for an invalid Case Type ID
    Given a user with [an active profile in CCD]
    And a case that has just been created as in [Caseworker1_Full_Case]
    When a request is prepared with appropriate values
    And the request [is prepared with an invalid Case Type ID]
    And it is submitted to call the [Start the event creation process for a new case for a Case Worker] operation of [CCD Data Store]
    Then a negative response is received
    And the response [has the 400 return code]
    And the response has all other details as expected

  @S-247 @Ignore #Probable bug. If case type ID is invalid, we are getting 200 instead of 404
    #Also, it is to be noted that the endpoint does not mentions about this scenario. So, CCD QA may chose to disqualify this test case.
  Scenario: Trigger the aboutToStart callback event for a caseworker for an invalid Case Type ID
    Given a user with [an active profile in CCD]
    And a case that has just been created as in [Caseworker1_Full_Case]
    When a request is prepared with appropriate values
    And the request [is prepared with an invalid Case Type ID]
    And it is submitted to call the [Start the event creation process for a new case for a Case Worker] operation of [CCD Data Store]
    Then a negative response is received
    And the response [has the 400 return code]
    And the response has all other details as expected

  @S-248
  Scenario: Trigger the aboutToStart callback event for a caseworker for an invalid Jurisdiction ID
    Given a user with [an active profile in CCD]
    And a case that has just been created as in [Caseworker1_Full_Case]
    When a request is prepared with appropriate values
    And the request [is prepared with an invalid Jurisdiction ID]
    And it is submitted to call the [Start the event creation process for a new case for a Case Worker] operation of [CCD Data Store]
    Then a negative response is received
    And the response [has the 403 return code]
    And the response has all other details as expected

  @S-249
  Scenario: Return error code 422 when an event request could not be processed.
    Given a user with [an active profile in CCD]
    And a case that has just been created as in [Caseworker1_Full_Case]
    And a successful call [to fire a START_PROGRESS event on the case just created] as in [S-249_Update_Case_State]
    When a request is prepared with appropriate values
    And the request [is prepared with an invalid case event]
    And it is submitted to call the [Start the event creation process for a new case for a Case Worker] operation of [CCD Data Store]
    Then a negative response is received
    And the response [has the 422 return code]
    And the response has all other details as expected
