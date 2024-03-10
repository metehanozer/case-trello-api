package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mt.utils.RandomUtil.getRandomFrom;
import static org.mt.utils.RandomUtil.getRandomStringStartsWith;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BoardAndCardTest extends BaseTest {

    static String boardId;
    static List<String> cardIdList = new ArrayList<>();

    @Test
    @Order(1)
    @Description("Check creating board with api")
    public void createBoard() {
        var boardName = getRandomStringStartsWith("board-", 15);
        var boardDesc = getRandomStringStartsWith("board description ", 35);

        boardId = RestAssured.given(requestSpec)
                .queryParam("name", boardName)
                .queryParam("desc", boardDesc)
                .post("/boards")
                .then()
                .spec(responseSpec)
                .body("name", equalTo(boardName))
                .body("desc", equalTo(boardDesc))
                .body("url", endsWith(boardName))
                .extract()
                .response()
                .jsonPath()
                .getString("id");

        verifyBoardIsExist();
    }

    @Order(2)
    @ParameterizedTest
    @MethodSource("getCardNameList")
    @Description("Check creating card with api")
    public void createCard(String cardName) {
        var todoListId = getToDoListIdOnBoard();

        var cardId = RestAssured.given(requestSpec)
                .queryParam("idList", todoListId)
                .queryParam("name", cardName)
                .post("/cards")
                .then()
                .spec(responseSpec)
                .body("name", equalTo(cardName))
                .body("url", endsWith(cardName))
                .extract()
                .response()
                .jsonPath()
                .getString("id");

        verifyCardIsExist(cardId, cardName);

        cardIdList.add(cardId);
    }

    private static Stream<String> getCardNameList() {
        return Stream.of(
                getRandomStringStartsWith("card-", 15),
                getRandomStringStartsWith("card-", 15));
    }

    @Test
    @Order(3)
    @Description("Check updating card with api")
    public void updateCard() {
        var cardId = getRandomFrom(cardIdList);
        var updatedCardName = getRandomStringStartsWith("card-", 15);

        RestAssured.given(requestSpec)
                .queryParam("name", updatedCardName)
                .put("/cards/" + cardId)
                .then()
                .spec(responseSpec)
                .extract()
                .response()
                .jsonPath()
                .getString("id");

        verifyCardIsExist(cardId, updatedCardName);
    }

    @Order(4)
    @ParameterizedTest
    @MethodSource("getCardIdList")
    @Description("Check deleting card with api")
    void deleteCard(String cardId) {
        RestAssured.given(requestSpec)
                .delete("/cards/" + cardId)
                .then()
                .spec(responseSpec);

        verifyCardIsNotExist(cardId);
    }

    private static Stream<String> getCardIdList() {
        return Stream.of(cardIdList.get(0), cardIdList.get(1));
    }

    @Test
    @Order(5)
    @Description("Check deleting board with api")
    void deleteBoard() {
        RestAssured.given(requestSpec)
                .delete("/boards/" + boardId)
                .then()
                .spec(responseSpec);

        verifyBoardIsNotExist();
    }

    @Step("Get to do list id on board")
    String getToDoListIdOnBoard() {
        return RestAssured.given(requestSpec)
                .get("/boards/" + boardId + "/lists")
                .then()
                .extract()
                .jsonPath()
                .getString("find { m -> m.name == 'To Do' }.id");
    }

    @Step("Verify card {cardId} is exist")
    void verifyCardIsExist(String cardId, String cardName) {
        RestAssured.given(requestSpec)
                .get("/cards/" + cardId)
                .then()
                .spec(responseSpec)
                .body("name", equalTo(cardName));
    }

    @Step("Verify card {cardId} is not exist")
    void verifyCardIsNotExist(String cardId) {
        RestAssured.given(requestSpec)
                .get("/cards/" + cardId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step("Verify board is exist")
    void verifyBoardIsExist() {
        RestAssured.given(requestSpec)
                .get("/boards/" + boardId)
                .then()
                .spec(responseSpec);
    }

    @Step("Verify board is not exist")
    void verifyBoardIsNotExist() {
        RestAssured.given(requestSpec)
                .get("/boards/" + boardId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
