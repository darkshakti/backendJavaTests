import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.margovladyko.dto.PostImageResponse;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static ru.margovladyko.Endpoints.*;

public class UpdateImageTests extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/f3.jpg";
    private MultiPartSpecification base64MultiPartSpec;
    private static String encodedFile;
    private String deleteHash;
    private String url;
    private String imageId;
    private String imageHash;
    private RequestSpecification requestSpecificationWithAuthWithBase64;
    private Response response;

    @BeforeEach
    void setUp() {
        byte[] byteArray = getFileContent(PATH_TO_IMAGE);
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
//        url = properties.getProperty("url");

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

    @DisplayName("Изменение title")
    @Test
    void updateImageInformationTest() {

        given()
                .spec(requestWithAuth)
                .param("title", "Heart")
                .when()
                .put(UPDATE_OR_GET_IMAGE, imageId)
                .prettyPeek()
                .then()
                .spec(positiveResponseSpecification);

        String title = given()
                .spec(requestWithAuth)
                .when()
                .get(UPDATE_OR_GET_IMAGE, imageId)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(PostImageResponse.class)
                .getData().getTitle();

        assertThat("Title", title, equalTo("Heart"));
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

    private byte[] getFileContent(String PATH_TO_IMAGE) {
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(this.PATH_TO_IMAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}
