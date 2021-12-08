import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class ImageTests extends BaseTest {
    private static final String PATH_TO_IMAGE = "src/test/resources/f3.jpg";
    static String encodedFile;
    String uploadedImageId;
    String url;
    String imageHash;

    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        url = properties.getProperty("url");
    }

    @Test
    void uploadFileBase64Test() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadFileImageTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", new File(PATH_TO_IMAGE))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

    }

    @Test
    void uploadFileImageByUrlTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", url)
                .multiPart("type", "url")
                .expect()
                .statusCode(200)
                .contentType("application/json")
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }


    @Test
    void updateImageInformationTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", url)
                .multiPart("type", "url")
                .multiPart("title", "white room")
                .expect()
                .statusCode(200)
                .contentType("application/json")
                .body("data.title", equalTo("white room"))
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void favoriteAnImageTest() {
        Response response = given()
                .headers("Authorization", token)
                .multiPart("image", new File(PATH_TO_IMAGE))
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response();

        imageHash = response.jsonPath().get("data.id");
        uploadedImageId = response.jsonPath().get("data.deletehash");

        given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", imageHash)
                .prettyPeek()
                .then()
                .extract()
                .response();
    }
    @Test
    void unfavoriteAnImageTest() {
        Response response = given()
                .headers("Authorization", token)
                .multiPart("image", new File(PATH_TO_IMAGE))
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response();

        imageHash = response.jsonPath().get("data.id");
        uploadedImageId = response.jsonPath().get("data.deletehash");

        given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", imageHash)
                .prettyPeek()
                .then()
                .extract()
                .response();
        given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", imageHash)
                .prettyPeek()
                .then()
                .extract()
                .response();
    }

    @AfterEach
    void tearDown() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", username, uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    private byte[] getFileContent() {
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(PATH_TO_IMAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}
