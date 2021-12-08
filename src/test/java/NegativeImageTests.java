import org.junit.jupiter.api.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class NegativeImageTests extends BaseTest {
    private static final String PATH_TO_IMAGE = "src/test/resources/f3.jpg";
    String uploadedImageId;

    @Test
    void uploadFileImageByWrongUrlTest() {
        given()
                .headers("Authorization", token)
                .multiPart("image", "https://youtube.com")
                .multiPart("type", "url")
                .expect()
                .statusCode(417)
                .contentType("application/json")
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek();

    }

    @Test
    void uploadNothingImageTest() {
        given()
                .headers("Authorization", token)
                .expect()
                .statusCode(400)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek();
    }

    @Test
    void uploadSomethingImageTest() {
        given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/test/resources/application.properties"))
                .expect()
                .statusCode(400)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek();
    }

    @Test
    void wrongImageTypeTest() {
        given()
                .headers("Authorization", token)
                .multiPart("image", new File(PATH_TO_IMAGE))
                .multiPart("type", "url")
                .expect()
                .statusCode(400)
                .contentType("application/json")
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek();
    }
}
