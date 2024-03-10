package steps;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mt.utils.RandomUtil.getRandomStringStartsWith;

public class BoardAndCardSteps extends BaseSteps {

    @Step("Create board on trello")
    public String createBoardOnTrello() {
        var boardName = getRandomStringStartsWith("board-", 15);
        var boardDesc = getRandomStringStartsWith("board description ", 35);

        var boardId = RestAssured.given(requestSpec)
                .queryParam("name", boardName)
                .queryParam("desc", boardDesc)
                .post("/boards")
                .then()
                .spec(responseSpec)
                .body("name", equalTo(boardName))
                .body("desc", equalTo(boardDesc))
                .body("url", endsWith(boardName))
                .body("closed", is(false))
                .extract()
                .response()
                .jsonPath()
                .getString("id");

        verifyBoardIsExist(boardId);

        return boardId;
    }

    @Step("Create card on board")
    public String createCardOnThis(String boardId) {
        var todoListId = getToDoListIdOnBoard(boardId);
        var cardName = getRandomStringStartsWith("card-", 15);

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

        return cardId;
    }

    @Step("Update card {cardId} on board")
    public void updateThis(String cardId) {
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

    @Step("Delete card {cardId} on board")
    public void deleteThis(String cardId) {
        RestAssured.given(requestSpec)
                .delete("/cards/" + cardId)
                .then()
                .spec(responseSpec);

        verifyCardIsNotExist(cardId);
    }

    @Step("Delete board {boardId} on trello")
    public void deleteBoardOnTrello(String boardId) {
        RestAssured.given(requestSpec)
                .delete("/boards/" + boardId)
                .then()
                .spec(responseSpec);

        verifyBoardIsNotExist(boardId);
    }

    @Step("Get to do list id on board {boardId}")
    String getToDoListIdOnBoard(String boardId) {
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

    @Step("Verify board {boardId} is exist")
    void verifyBoardIsExist(String boardId) {
        RestAssured.given(requestSpec)
                .get("/boards/" + boardId)
                .then()
                .spec(responseSpec);
    }

    @Step("Verify board {boardId} is not exist")
    void verifyBoardIsNotExist(String boardId) {
        RestAssured.given(requestSpec)
                .get("/boards/" + boardId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
