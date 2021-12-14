import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class AccountTests extends BaseTest {
    @Test
    void getAccountInfoTest() {
        given(requestWithAuth, positiveResponseSpecification)
                .get("/account/{username}", username);
    }

    @Test
    void getAccountAccountWithLoggingTest() {
        given()
                .spec(requestWithAuth)
                .log()
                .method()
                .log()
                .uri()
                .when()
                .get("https://api.imgur.com/3/account/{username}", username)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void getAccountInfoWithAssertionsInGivenTest() {
        given()
                .spec(requestWithAuth)
                .log()
                .method()
                .log()
                .uri()
                .expect()
                .statusCode(200)
                .contentType("application/json")
                .body("data.url", equalTo(username))
                .body("success", equalTo(true))
                .body("status", equalTo(200))
                .when()
                .get("https://api.imgur.com/3/account/{username}", username)
                .prettyPeek();

    }

    @Test
    void getAccountInfoWithAssertionsAfterTest() {
        Response response = given()
                .spec(requestWithAuth)
                .log()
                .method()
                .log()
                .uri()
                .when()
                .get("https://api.imgur.com/3/account/{username}", username)
                .prettyPeek();
        assertThat(response.jsonPath().get("data.url"), equalTo(username));
        assertThat(response.statusCode(), equalTo(200));
    }
}
