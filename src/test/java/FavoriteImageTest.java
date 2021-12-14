import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static ru.margovladyko.Endpoints.*;

public class FavoriteImageTest extends BaseTest {

    private final String PATH_TO_IMAGE = "src/test/resources/f3.jpg";
    private MultiPartSpecification base64MultiPartSpec;
    private static String encodedFile;
    private String deleteHash;
    private String imageId;
    private RequestSpecification requestSpecificationWithAuthWithBase64;
    private Response response;

    @BeforeEach
    void setUp() {
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);

        base64MultiPartSpec = new MultiPartSpecBuilder(encodedFile)
                .controlName("image")
                .build();

        requestSpecificationWithAuthWithBase64 = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart(base64MultiPartSpec)
                .build();

        response = given(requestSpecificationWithAuthWithBase64, positiveResponseSpecification)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response();

        imageId = response.jsonPath().getString("data.id");
        deleteHash = response.jsonPath().getString("data.deletehash");
    }

    @Test
    void favoriteAnImageTest() {
        given()
                .spec(requestWithAuth)
                .when()
                .post(FAVORITE_IMAGE, imageId)
                .prettyPeek()
                .then()
                .spec(positiveResponseSpecification);
    }

    @Test
    void unfavoriteAnImageTest() {
        given()
                .spec(requestWithAuth)
                .when()
                .post(FAVORITE_IMAGE, imageId)
                .prettyPeek()
                .then()
                .spec(positiveResponseSpecification);

        given()
                .spec(requestWithAuth)
                .when()
                .post(FAVORITE_IMAGE, imageId)
                .prettyPeek()
                .then()
                .spec(positiveResponseSpecification);
    }

    @AfterEach
    void tearDown() {
        given()
                .headers("Authorization", token)
                .when()
                .delete(DELETE_IMAGE, username, deleteHash)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    private byte[] getFileContent() {
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(this.PATH_TO_IMAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}
