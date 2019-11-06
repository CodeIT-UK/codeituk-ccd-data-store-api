package uk.gov.hmcts.ccd.fta.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class BackEndFunctionalTestScenarioPlayer {

    private final BackEndFunctionalTestScenarioContext scenarioContext;

    public BackEndFunctionalTestScenarioPlayer() {
        scenarioContext = new BackEndFunctionalTestScenarioContext();
    }

    @Given("an appropriate test context as detailed in the test data source")
    public void anAppropriateTestContextAsDetailedInTheTestDataSource() {
        boolean isTestDataLoaded = scenarioContext.loadTestData();
        assertThat(isTestDataLoaded).isTrue();
    }

    @Given("a user with {}")
    public void aUserWithProfile(String profileType) {

    }

    @When("a request is prepared with appropriate values")
    public void aRequestIsPreparedWithAppropriateValues() {

    }

    @When("it is submitted to call the {} operation of {}")
    public void itIsSubmittedToCallTheOperationOf(String operation, String productName) {

    }

    @Then("a positive response is received")
    public void aPositiveResponseIsReceived() {

    }

    @Then("a negative response is received")
    public void aNegativeResponseIsReceived() {

    }

    @Then("the response {}")
    public void theResponse(String responseAssertion) {

    }
}
