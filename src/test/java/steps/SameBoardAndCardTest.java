package steps;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mt.utils.RandomUtil.getRandomFrom;

@Epic("Create Board Card")
public class SameBoardAndCardTest {

    BoardAndCardSteps testSteps = new BoardAndCardSteps();

    /**
     TRELLO.COM DA BİR TANE PANO EKLİYOR. PANODA TO DO LİSTE İKİ TANE KART EKLİYOR.
     KARTLARDAN RASTGELE BİRİNİ SEÇİP ADINI GÜNCELLİYOR. KARTLARI VE PANOYU SİLİYOR.
     */
    @Test
    @Description("Check trello board and card crud test with api.")
    void checkTrelloBoardAndCard() {
        var board = testSteps.createBoardOnTrello();
        var card1 = testSteps.createCardOnThis(board);
        var card2 = testSteps.createCardOnThis(board);

        var randomCard = getRandomFrom(List.of(card1, card2));
        testSteps.updateThis(randomCard);

        testSteps.deleteThis(card1);
        testSteps.deleteThis(card2);
        testSteps.deleteBoardOnTrello(board);
    }
}
