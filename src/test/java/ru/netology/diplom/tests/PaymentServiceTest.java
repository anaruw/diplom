package ru.netology.diplom.tests;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.diplom.pages.DashBoardPage;
import ru.netology.diplom.pages.PaymentPage;
import ru.netology.diplom.util.DataHelper;
import ru.netology.diplom.util.SqlHelper;

public class PaymentServiceTest {
    DashBoardPage dashBoardPage;
    PaymentPage paymentPage;

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
        paymentPage = dashBoardPage.toPaymentForm();
    }

    @Test
    public void approvedPaymentTest() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String expectedTransactionStatus = "APPROVED";

        Assertions.assertAll(
                ()-> paymentPage.checkSuccessNotification(),
                ()-> Assertions.assertEquals(expectedTransactionStatus, SqlHelper.paymentStatus())
        );
    }

    @Test
    public void declinedPaymentTest() {
        String cardNumber = DataHelper.declinedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorNotification = "Ошибка! Банк отказал в проведении операции.";

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String expectedTransactionStatus = "DECLINED";

        Assertions.assertAll(
                ()-> paymentPage.checkErrorNotification(errorNotification),
                ()-> Assertions.assertEquals(expectedTransactionStatus, SqlHelper.paymentStatus())
        );
    }

    @Test
    public void paymentWithRandomCardNumber() {
        String cardNumber = DataHelper.randomCardNumber();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorNotification = "Ошибка! Данные введены неверно, или отсутствует связь с банком.";

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        Assertions.assertAll(
                ()-> paymentPage.checkErrorNotification(errorNotification),
                ()-> Assertions.assertEquals(0, SqlHelper.ordersCount()),
                ()-> Assertions.assertEquals(0, SqlHelper.paymentCount()),
                ()-> Assertions.assertEquals(0, SqlHelper.creditCount())
        );
    }

    @Test
    public void paymentWithIncompleteCardNumber() {
        String cardNumber = DataHelper.incompleteCardNumber();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidCardNumberMessage();
    }

    @Test
    public void paymentWithEmptyCardNumberField() {
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidCardNumberMessage();
    }

    @Test
    public void paymentWithZeroCardNumber() {
        String cardNumber = DataHelper.zeroCardNumber();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidCardNumberMessage();
    }

    @Test
    public void paymentWithLongCardNumber() {
        String cardNumber = DataHelper.incompleteCardNumber() + DataHelper.twoRandomDigits();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String actualCardNumberInput = paymentPage.getCardNumberInput();

        Assertions.assertNotEquals(cardNumber, actualCardNumberInput);
    }

    @Test
    public void paymentWithLetterInCardNumber() {
        String cardNumber = DataHelper.incompleteCardNumber() + DataHelper.randomLetter();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String actualCardNumberInput = paymentPage.getCardNumberInput();

        Assertions.assertNotEquals(cardNumber, actualCardNumberInput);
    }

    @Test
    public void paymentWithSpecialCharInCardNumber() {
        String cardNumber = DataHelper.incompleteCardNumber() + DataHelper.randomSpecialChar();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String actualCardNumberInput = paymentPage.getCardNumberInput();

        Assertions.assertNotEquals(cardNumber, actualCardNumberInput);
    }

    @Test
    public void paymentWithMinCardExpiry() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.minCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String expectedTransactionStatus = "APPROVED";

        Assertions.assertAll(
                ()-> paymentPage.checkSuccessNotification(),
                ()-> Assertions.assertEquals(expectedTransactionStatus, SqlHelper.paymentStatus())
        );
    }

    @Test
    public void paymentWithMaxCardExpiry() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.maxCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String expectedTransactionStatus = "APPROVED";

        Assertions.assertAll(
                ()-> paymentPage.checkSuccessNotification(),
                ()-> Assertions.assertEquals(expectedTransactionStatus, SqlHelper.paymentStatus())
        );
    }

    @Test
    public void paymentWithExpiredCardExpiry() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.expiredCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Истёк срок действия карты";

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidYearMessage(errorMessage);
    }

    @Test
    public void paymentWithUnavailableCardExpiry() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.unavailableCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Неверно указан срок действия карты";

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidYearMessage(errorMessage);
    }

    @Test
    public void paymentWithEmptyYearField() {
        String cardNumber = DataHelper.approvedCard();
        String month = DataHelper.validMonth();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Неверно указан срок действия карты";

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setMonthInput(month);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidYearMessage(errorMessage);
    }

    @Test
    public void paymentWithEmptyMonthField() {
        String cardNumber = DataHelper.approvedCard();
        String year = DataHelper.validYear();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Неверный формат";

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setYearInput(year);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidMonthMessage(errorMessage);
    }

    @Test
    public void paymentWithUnavailableMonth() {
        String cardNumber = DataHelper.approvedCard();
        String month = DataHelper.invalidMonth();
        String year = DataHelper.validYear();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Неверно указан срок действия карты";

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setMonthInput(month);
        paymentPage.setYearInput(year);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidMonthMessage(errorMessage);
    }

    @Test
    public void paymentWithZeroMonth() {
        String cardNumber = DataHelper.approvedCard();
        String month = DataHelper.zeroMonth();
        String year = DataHelper.validYear();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Неверно указан срок действия карты";

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setMonthInput(month);
        paymentPage.setYearInput(year);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidMonthMessage(errorMessage);
    }

    @Test
    public void paymentWithMinOwnerLength() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.minOwnerLength();
        String cvcCode = DataHelper.randomCvcCode();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String expectedTransactionStatus = "APPROVED";

        Assertions.assertAll(
                ()-> paymentPage.checkSuccessNotification(),
                ()-> Assertions.assertEquals(expectedTransactionStatus, SqlHelper.paymentStatus())
        );
    }

    @Test
    public void paymentWithMaxOwnerLength() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.maxOwnerLength();
        String cvcCode = DataHelper.randomCvcCode();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String expectedTransactionStatus = "APPROVED";

        Assertions.assertAll(
                ()-> paymentPage.checkSuccessNotification(),
                ()-> Assertions.assertEquals(expectedTransactionStatus, SqlHelper.paymentStatus())
        );
    }

    @Test
    public void paymentWithOneLetterOwnerName() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomLetter();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Заполните, как указано на Вашей карте.";

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidOwnerMessage(errorMessage);
    }

    @Test
    public void paymentWithUnavailableOwnerName() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.unavailableOwnerLength();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Заполните, как указано на Вашей карте.";

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidOwnerMessage(errorMessage);
    }

    @Test
    public void paymentWithEmptyOwnerField() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Поле обязательно для заполнения";

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidOwnerMessage(errorMessage);
    }

    @Test
    public void paymentWithRusOwnerName() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomRusOwner();
        String cvcCode = DataHelper.randomCvcCode();

        String errorMessage = "Заполните, как указано на Вашей карте.";

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidOwnerMessage(errorMessage);
    }

    @Test
    public void paymentWithSpecialCharInOwnerName() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner() + DataHelper.randomSpecialChar();
        String cvcCode = DataHelper.randomCvcCode();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String actualOwnerInput = paymentPage.getOwnerInput();

        Assertions.assertNotEquals(owner, actualOwnerInput);
    }

    @Test
    public void paymentWithDigitsInOwnerName() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner() + DataHelper.twoRandomDigits();
        String cvcCode = DataHelper.randomCvcCode();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String actualOwnerInput = paymentPage.getOwnerInput();

        Assertions.assertNotEquals(owner, actualOwnerInput);
    }

    @Test
    public void paymentWithEmptyCvcField() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.sendForm();

        paymentPage.checkInvalidCvcMessage();
    }

    @Test
    public void paymentWithZeroCvcCode() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.zeroCvcCode();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidCvcMessage();
    }

    @Test
    public void paymentWithIncompleteCvcCode() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.twoRandomDigits();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        paymentPage.checkInvalidCvcMessage();
    }

    @Test
    public void paymentWithSpecialCharInCvcCode() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.twoRandomDigits() + DataHelper.randomSpecialChar();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String actualCvcInput = paymentPage.getCvcInput();

        Assertions.assertNotEquals(cvcCode, actualCvcInput);
    }

    @Test
    public void paymentWithLetterInCvcCode() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.twoRandomDigits() + DataHelper.randomLetter();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String actualCvcInput = paymentPage.getCvcInput();

        Assertions.assertNotEquals(cvcCode, actualCvcInput);
    }

    @Test
    public void paymentWithTooLongCvcCode() {
        String cardNumber = DataHelper.approvedCard();
        String cardExpiry = DataHelper.currentDateCardExpiry();
        String owner = DataHelper.randomOwner();
        String cvcCode = DataHelper.twoRandomDigits() + DataHelper.twoRandomDigits();

        paymentPage.setCardNumberInput(cardNumber);
        paymentPage.setCardExpiryInput(cardExpiry);
        paymentPage.setOwnerInput(owner);
        paymentPage.setCvcInput(cvcCode);
        paymentPage.sendForm();

        String actualCvcInput = paymentPage.getCvcInput();

        Assertions.assertNotEquals(cvcCode, actualCvcInput);
    }
}