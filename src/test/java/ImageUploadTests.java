import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.margovladyko.dto.PostImageResponse;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static ru.margovladyko.Endpoints.DELETE_IMAGE;
import static ru.margovladyko.Endpoints.UPLOAD_IMAGE;


public class ImageUploadTests extends BaseTest {
    private static final String PATH_TO_IMAGE = "src/test/resources/f3.jpg";
    private String encodedFile;
    private String url;
    private String deleteHash;

    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        url = properties.getProperty("url");
    }

    @Test
    void uploadFileBase64Test() {
        deleteHash = given()
                .spec(requestWithAuth)
                .multiPart("image", encodedFile)
                .when()
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .spec(positiveResponseSpecification)
                .extract()
                .response()
                .body()
                .as(PostImageResponse.class)
                .getData().getDeletehash();
    }

    @Test
    void uploadFileImageTest() {
        deleteHash = given()
                .spec(requestWithAuth)
                .multiPart("image", new File(PATH_TO_IMAGE))
                .when()
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .spec(positiveResponseSpecification)
                .extract()
                .response()
                .body()
                .as(PostImageResponse.class)
                .getData().getDeletehash();

    }

    @Test
    void uploadFileImageByUrlTest() {
        deleteHash = given()
                .spec(requestWithAuth)
                .multiPart("image", url)
                .multiPart("type", "url")
                .when()
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .spec(positiveResponseSpecification)
                .extract()
                .response()
                .body()
                .as(PostImageResponse.class)
                .getData().getDeletehash();
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
