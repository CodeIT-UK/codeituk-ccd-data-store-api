#==================================================
@F-126 @Ignore
Feature: F-126: Delete Cases (For Cleanup)
#==================================================

Background: Load test data for the scenario
    Given an appropriate test context as detailed in the test data source

#-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
@S-126.1
Scenario: must delete cases to be cleaned up and only those ones
    Given a user with [an admin profile on CCD],
     When a request is prepared with appropriate values,
      And the request [is to delete cases to be cleaned up],
      And it is submitted to call the [Delete Cases] operation of [Data Store API],
     Then a positive response is received,
      And the response [reports that the cases have been deleted],
      And the response has all other details as expected.
#-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


@S-126.10
Scenario: must get an error in an attempt to delete cases with a non-admin user
    Given a user with [an non-admin profile on CCD],

