import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest {

    @BeforeAll //before all tests in this class to keep tests self-contained so tests classes do not depend on each other
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnPatientsWithValidToken() {
        String loginPayload = """
                    {
                        "email": "testuser@test.com",
                        "password": "password123"
                    }
                """; //defining properties we are going to send in the request that our test runs

        // 2. Act
        String token = given() //arrange step
                .contentType("application/json")
                .body(loginPayload)
                .when() //step 2 act
                .post("/auth/login") //act step b/c it triggers a post request to the login endpoint on auth service
                .then() //step 3 assert
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("token"); //extracting the response and using JSONPath to pick out the token from the response

        Response response = given()
                .header("Authorization", "Bearer " + token) //test set up
                .when() //act on what we are testing
                .get("/api/patients")
                .then()
                .statusCode(200)
                .body("patients", notNullValue()) //assert has a patients property that is not null
                .extract()
                .response();
        System.out.println("Patients: " + response.getBody().asString());
    }
}
