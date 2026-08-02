import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {
    //specify the base url of all our requests so our tests know our api gateway
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    /* 3 steps to create a good test (regardless of unit or integration test) 3 A's
    * 1. Arrange > do any set up (like data) that this test needs in order to work 100% of the time
    * 2. Act > code we write that triggers the thing we are testing
    * 3. Assert > assert the result from stage 2 ex: assert response has a valid token and OK status
    * */

    //HAPPY path test > when all the inputs are good and the response codes and data in response are good
    @Test //naming convention: should (an expected outcome) with this data
    public void shouldReturnOKWithValidToken() {
        // 1. Arrange > pass in an email and password
        String loginPayload = """
                    {
                        "email": "testuser@test.com",
                        "password": "password123"
                    }
                """; //defining properties we are going to send in the request that our test runs

        // 2. Act
        Response response = given() //arrange step
                .contentType("application/json")
                .body(loginPayload)
                .when() //step 2 act
                .post("/auth/login") //act step b/c it triggers a post request to the login endpoint on auth service
                .then() //step 3 assert
                .statusCode(200)
                .body("token", notNullValue()) //asserting status code and not null token
                .extract().response();
        System.out.println("Generated Token: " + response.jsonPath().getString("token"));
    }

    @Test // SAD Path test
    public void shouldReturnUnauthorizedOnInvalidLogin() {
        // 1. Arrange > pass in an email and password
        String loginPayload = """
                    {
                        "email": "invalid_user@test.com",
                        "password": "wrongpassword"
                    }
                """; //defining properties we are going to send in the request that our test runs

        // 2. Act
        given() //arrange step
                .contentType("application/json")
                .body(loginPayload)
                .when() //step 2 act
                .post("/auth/login") //act step b/c it triggers a post request to the login endpoint on auth service
                .then() //step 3 assert
                .statusCode(401);
    }
}
