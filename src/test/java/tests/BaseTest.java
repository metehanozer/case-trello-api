package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.mt.utils.PropsManager;

import static org.hamcrest.Matchers.*;

public class BaseTest {

    protected static RequestSpecification requestSpec;
    protected static ResponseSpecification responseSpec;

    static String baseUri = PropsManager.getInstance().get("base.uri");
    static String basePath = PropsManager.getInstance().get("base.path");
    static String apiKey = PropsManager.getInstance().get("api.key");
    static String apiToken = PropsManager.getInstance().get("api.token");

    @BeforeAll
    static void setupRestAssuredBeforeAll() {
        setupRequestSpec();
        setupResponseSpec();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    static void setupRequestSpec() {
        requestSpec = RestAssured.given()
                .baseUri(baseUri)
                .basePath(basePath)
                .contentType(ContentType.JSON)
                .queryParam("key", apiKey)
                .queryParam("token", apiToken)
                .filter(new AllureRestAssured());
    }

    static void setupResponseSpec() {
        responseSpec = RestAssured.expect()
                .statusCode(anyOf(is(HttpStatus.SC_OK),
                        is(HttpStatus.SC_CREATED),
                        is(HttpStatus.SC_ACCEPTED),
                        is(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION),
                        is(HttpStatus.SC_NO_CONTENT)))
                .contentType(ContentType.JSON)
                .time(lessThan(5000L))
                .defaultParser(Parser.JSON);
    }
}
