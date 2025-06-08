package ru.netology.diplom.tests;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.diplom.pages.CreditPage;
import ru.netology.diplom.pages.DashBoardPage;
import ru.netology.diplom.util.DataHelper;
import ru.netology.diplom.util.SqlHelper;

public class CreditServiceTest {
    DashBoardPage dashBoardPage;
    CreditPage creditPage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
        SqlHelper.cleaningDB();
    }

    @BeforeEach
    public void setUp() {
        SqlHelper.cleaningDB();
        dashBoardPage = Selenide.open("http://localhost:8080", DashBoardPage.class);
        creditPage = dashBoardPage.toCreditForm();
    }

    @Test
    public void approvedCreditTest() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedTransactionStatus = "APPROVED";

        Assertions.assertAll(
                ()-> creditPage.checkSuccessNotification(),
                ()-> Assertions.assertEquals(expectedTransactionStatus, SqlHelper.creditStatus())
        );
    }

    @Test
    public void declinedCreditTest() {
        String cardNumber = DataHelper.declinedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorNotification = "Ошибка! Банк отказал в проведении операции.";

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedTransactionStatus = "DECLINED";

        Assertions.assertAll(
                ()-> creditPage.checkErrorNotification(errorNotification),
                ()-> Assertions.assertEquals(expectedTransactionStatus, SqlHelper.creditStatus())
        );
    }

    @Test
    public void creditWithRandomCardNumber() {
        String cardNumber = DataHelper.randomCardNumber();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorNotification = "Ошибка! Данные введены неверно, или отсутствует связь с банком.";

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        Assertions.assertAll(
                ()-> creditPage.checkErrorNotification(errorNotification),
                ()-> Assertions.assertEquals(0, SqlHelper.ordersCount()),
                ()-> Assertions.assertEquals(0, SqlHelper.paymentCount()),
                ()-> Assertions.assertEquals(0, SqlHelper.creditCount())
        );
    }

    @Test
    public void creditWithIncompleteCardNumber() {
        String cardNumber = DataHelper.incompleteCardNumber();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidCardNumberMessage();
    }

    @Test
    public void creditWithEmptyCardNumberField() {
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidCardNumberMessage();
    }

    @Test
    public void creditWithZeroCardNumber() {
        String cardNumber = DataHelper.zeroCardNumber();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidCardNumberMessage();
    }

    @Test
    public void creditWithLongCardNumber() {
        String cardNumber = DataHelper.incompleteCardNumber() + DataHelper.twoRandomDigits();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedCardNumber = cardNumber.substring(0, cardNumber.length() - 1);
        creditPage.checkCardNumberInput(expectedCardNumber);
    }

    @Test
    public void creditWithLetterInCardNumber() {
        String cardNumber = DataHelper.incompleteCardNumber() + DataHelper.randomLetter();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedCardNumber = cardNumber.substring(0, cardNumber.length() - 1);
        creditPage.checkCardNumberInput(expectedCardNumber);
    }

    @Test
    public void creditWithSpecialCharInCardNumber() {
        String cardNumber = DataHelper.incompleteCardNumber() + DataHelper.randomSpecialChar();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedCardNumber = cardNumber.substring(0, cardNumber.length() - 1);
        creditPage.checkCardNumberInput(expectedCardNumber);
    }

    @Test
    public void creditWithMinCardExpiry() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.minCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedTransactionStatus = "APPROVED";

        Assertions.assertAll(
                ()-> creditPage.checkSuccessNotification(),
                ()-> Assertions.assertEquals(expectedTransactionStatus, SqlHelper.creditStatus())
        );
    }

    @Test
    public void creditWithMaxCardExpiry() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.maxCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedTransactionStatus = "APPROVED";

        Assertions.assertAll(
                ()-> creditPage.checkSuccessNotification(),
                ()-> Assertions.assertEquals(expectedTransactionStatus, SqlHelper.creditStatus())
        );
    }

    @Test
    public void creditWithExpiredCardExpiry() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.expiredCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Истёк срок действия карты";

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidYearMessage(errorMessage);
    }

    @Test
    public void creditWithUnavailableCardExpiry() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.unavailableCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Неверно указан срок действия карты";

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidYearMessage(errorMessage);
    }

    @Test
    public void creditWithEmptyYearField() {
        String cardNumber = DataHelper.approvedCard();
        String month = DataHelper.validMonth();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Неверный формат";

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setMonthInput(month);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidYearMessage(errorMessage);
    }

    @Test
    public void creditWithEmptyMonthField() {
        String cardNumber = DataHelper.approvedCard();
        String year = DataHelper.validYear();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Неверный формат";

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setYearInput(year);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidMonthMessage(errorMessage);
    }

    @Test
    public void creditWithUnavailableMonth() {
        String cardNumber = DataHelper.approvedCard();
        String month = DataHelper.invalidMonth();
        String year = DataHelper.validYear();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Неверно указан срок действия карты";

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setMonthInput(month);
        creditPage.setYearInput(year);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidMonthMessage(errorMessage);
    }

    @Test
    public void creditWithZeroMonth() {
        String cardNumber = DataHelper.approvedCard();
        String month = DataHelper.zeroMonth();
        String year = DataHelper.validYear();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Неверно указан срок действия карты";

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setMonthInput(month);
        creditPage.setYearInput(year);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidMonthMessage(errorMessage);
    }

    @Test
    public void creditWithMinOwnerLength() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.minOwnerLength();
        String cvcCode = DataHelper.randomCvcCode();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedTransactionStatus = "APPROVED";

        Assertions.assertAll(
                ()-> creditPage.checkSuccessNotification(),
                ()-> Assertions.assertEquals(expectedTransactionStatus, SqlHelper.creditStatus())
        );
    }

    @Test
    public void creditWithMaxOwnerLength() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.maxOwnerLength();
        String cvcCode = DataHelper.randomCvcCode();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedTransactionStatus = "APPROVED";

        Assertions.assertAll(
                ()-> creditPage.checkSuccessNotification(),
                ()-> Assertions.assertEquals(expectedTransactionStatus, SqlHelper.creditStatus())
        );
    }

    @Test
    public void creditWithOneLetterOwnerName() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomLetter();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Заполните, как указано на Вашей карте.";

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidOwnerMessage(errorMessage);
    }

    @Test
    public void creditWithUnavailableOwnerName() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.unavailableOwnerLength();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Заполните, как указано на Вашей карте.";

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidOwnerMessage(errorMessage);
    }

    @Test
    public void creditWithEmptyOwnerField() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Поле обязательно для заполнения";

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidOwnerMessage(errorMessage);
    }

    @Test
    public void creditWithRusOwnerName() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomRusOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Заполните, как указано на Вашей карте.";

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidOwnerMessage(errorMessage);
    }

    @Test
    public void creditWithSpecialCharInOwnerName() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner() + DataHelper.randomSpecialChar();
        String cvcCode = DataHelper.randomCvcCode();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedOwner = owner.substring(0, owner.length() - 1);
        creditPage.checkOwnerInput(expectedOwner);
    }

    @Test
    public void creditWithDigitsInOwnerName() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner() + DataHelper.twoRandomDigits();
        String cvcCode = DataHelper.randomCvcCode();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedOwner = owner.substring(0, owner.length() - 1);
        creditPage.checkOwnerInput(expectedOwner);
    }

    @Test
    public void creditWithEmptyCvcField() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.sendForm();

        creditPage.checkInvalidCvcMessage();
    }

    @Test
    public void creditWithZeroCvcCode() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.zeroCvcCode();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidCvcMessage();
    }

    @Test
    public void creditWithIncompleteCvcCode() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.twoRandomDigits();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        creditPage.checkInvalidCvcMessage();
    }

    @Test
    public void creditWithSpecialCharInCvcCode() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.twoRandomDigits() + DataHelper.randomSpecialChar();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedCvcCode = cvcCode.substring(0, cvcCode.length() - 1);
        creditPage.checkCvcInput(expectedCvcCode);
    }

    @Test
    public void creditWithLetterInCvcCode() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.twoRandomDigits() + DataHelper.randomLetter();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedCvcCode = cvcCode.substring(0, cvcCode.length() - 1);
        creditPage.checkCvcInput(expectedCvcCode);
    }

    @Test
    public void creditWithTooLongCvcCode() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.twoRandomDigits() + DataHelper.twoRandomDigits();

        creditPage.setCardNumberInput(cardNumber);
        creditPage.setCardExpiryInput(cardExpiry);
        creditPage.setOwnerInput(owner);
        creditPage.setCvcInput(cvcCode);
        creditPage.sendForm();

        String expectedCvcCode = cvcCode.substring(0, cvcCode.length() - 1);
        creditPage.checkCvcInput(expectedCvcCode);
    }
}