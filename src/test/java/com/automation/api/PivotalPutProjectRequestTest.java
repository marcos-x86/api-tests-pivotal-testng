package com.automation.api;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PivotalPutProjectRequestTest {

    String endpointBase = "https://www.pivotaltracker.com/services/v5/projects";
    String TOKEN = "";
    String projectId = "";

    @BeforeMethod
    public void createProject() {
        // Define headers using map data structure.
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TrackerToken", TOKEN);
        headers.put("Content-Type", "application/json");

        // Define request body.
        String body = "{\"name\": \"AutomationProject 2\"}";

        // Send the POST request and store response in the responseProject variable.
        Response responseProject = RestAssured.given().headers(headers).body(body).when().post(endpointBase);

        // Verifies status code.
        int responseStatusCode = responseProject.statusCode();
        int expectedStatusCode = 200;
        System.out.println("This is the status code of the response: " + responseStatusCode);
        Assert.assertEquals(responseStatusCode, expectedStatusCode);

        // Extracts ID from created project.
        projectId = responseProject.jsonPath().getString("id");
    }

    @Test
    public void putProjectTest() {
        // Define headers using map data structure.
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TrackerToken", TOKEN);
        headers.put("Content-Type", "application/json");

        // Define request body.
        String body = "{\"name\": \"Automation Modified Name\"}";

        // Set endpoint.
        String putEndpoint = endpointBase + "/" + projectId;

        // Send the PUT request and store response in the responseProject variable.
        Response responseProject = RestAssured.given().headers(headers).body(body).when().put(putEndpoint);

        // Verifies status code.
        int responseStatusCode = responseProject.statusCode();
        int expectedStatusCode = 200;
        System.out.println("This is the status code of the response: " + responseStatusCode);
        Assert.assertEquals(responseStatusCode, expectedStatusCode);

        // Verifies that the response body matches with Json Schema.
        File jsonSchemaFile = new File("src/test/resources/schemas/projectPUTSchema.json");
        responseProject.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(jsonSchemaFile));

        // Verifies that response body "name" field has the expected value.
        String responseBody = responseProject.body().asString();
        System.out.println("This is the body response: " + responseBody);
        String actualName = responseProject.jsonPath().getString("name");
        String expectedName = "Automation Modified Name";
        Assert.assertEquals(actualName, expectedName);
    }

    @AfterMethod
    public void deleteProject() {
        // Define headers using map data structure.
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TrackerToken", TOKEN);
        headers.put("Content-Type", "application/json");

        // Define endpoint.
        String endpoint = "https://www.pivotaltracker.com/services/v5/projects/" + projectId;

        // Send delete request.
        RestAssured.given().headers(headers).when().delete(endpoint);
    }
}
