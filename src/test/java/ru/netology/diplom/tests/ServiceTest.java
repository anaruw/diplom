package ru.netology.diplom.tests;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.diplom.pages.CreditPage;
import ru.netology.diplom.pages.DashBoardPage;
import ru.netology.diplom.pages.PaymentPage;
import ru.netology.diplom.util.DataHelper;
import ru.netology.diplom.util.SqlHelper;

import java.sql.Timestamp;

public class ServiceTest {
    DashBoardPage dashBoardPage;
    DataHelper.CardInfo testData;

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
        dashBoardPage = Selenide.open("http://localhost:8080", DashBoardPage.class);
        testData = DataHelper.cardInfo();
    }

    @Test
    public void shouldBeInvalidCardNumberMessageByPayment() {
        testData = testData.withCardNumber(testData.getCardNumber().substring(1));
        PaymentPage formPage = dashBoardPage.toPaymentForm();

        formPage.inputData(testData);
        formPage.sendForm();
        formPage.checkInvalidCardNumberMessage();
    }

    @Test
    public void shouldBeInvalidCardNumberMessageByCredit() {
        testData = testData.withCardNumber(testData.getCardNumber().substring(1));
        CreditPage formPage = dashBoardPage.toCreditForm();

        formPage.inputData(testData);
        formPage.sendForm();
        formPage.checkInvalidCardNumberMessage();
    }

    @Test
    public void shouldBeInvalidMonthMessageByPayment() {
        PaymentPage formPage = dashBoardPage.toPaymentForm();

        formPage.inputDataWithInvalidMonth(testData);
        formPage.sendForm();
        formPage.checkInvalidMonthMessage();
    }

    @Test
    public void shouldBeInvalidMonthMessageByCredit() {
        CreditPage formPage = dashBoardPage.toCreditForm();

        formPage.inputDataWithInvalidMonth(testData);
        formPage.sendForm();
        formPage.checkInvalidMonthMessage();
    }

    @Test
    public void shouldBeInvalidYearMessageByPayment() {
        String messageContent = "Истёк срок действия карты";
        testData = testData.withCardExpiry(DataHelper.expiredCardExpiry());
        PaymentPage formPage = dashBoardPage.toPaymentForm();

        formPage.inputData(testData);
        formPage.sendForm();
        formPage.checkInvalidYearMessage(messageContent);
    }

    @Test
    public void shouldBeInvalidYearMessageByCredit() {
        String messageContent = "Неверно указан срок действия карты";
        testData = testData.withCardExpiry(DataHelper.unavailableCardExpiry());
        CreditPage formPage = dashBoardPage.toCreditForm();

        formPage.inputData(testData);
        formPage.sendForm();
        formPage.checkInvalidYearMessage(messageContent);
    }

    @Test
    public void shouldBeInvalidOwnerMessageWithEmptyInput() {
        String invalidInputMessage = "Поле обязательно для заполнения";

        testData = testData.withOwner("");
        PaymentPage formPage = dashBoardPage.toPaymentForm();

        formPage.inputData(testData);
        formPage.sendForm();
        formPage.checkInvalidOwnerMessage(invalidInputMessage);
    }

    @Test
    public void shouldBeInvalidOwnerMessageWithRuLocale() {
        String invalidInputMessage = "Можно использовать только латинские буквы";

        testData = testData.withOwner(DataHelper.randomRusOwner());
        CreditPage formPage = dashBoardPage.toCreditForm();

        formPage.inputData(testData);
        formPage.sendForm();
        formPage.checkInvalidOwnerMessage(invalidInputMessage);
    }

    @Test
    public void shouldBeInvalidCvcCodeMessageByPayment() {
        testData = testData.withCode(testData.getCode().substring(1));
        PaymentPage formPage = dashBoardPage.toPaymentForm();

        formPage.inputData(testData);
        formPage.sendForm();
        formPage.checkInvalidCvcMessage();
    }

    @Test
    public void shouldBeInvalidCvcCodeMessageByCredit() {
        testData = testData.withCode(testData.getCode().substring(1));
        CreditPage formPage = dashBoardPage.toCreditForm();

        formPage.inputData(testData);
        formPage.sendForm();
        formPage.checkInvalidCvcMessage();
    }

    @Test
    public void paymentWithApprovedCardTest() {
        Timestamp testCreated = new Timestamp(System.currentTimeMillis());

        testData = testData.withCardNumber(DataHelper.approvedCard());
        PaymentPage formPage = dashBoardPage.toPaymentForm();

        formPage.inputData(testData);
        formPage.sendForm();

        Assertions.assertAll(
                () -> formPage.checkSuccessNotification(),
                () -> Assertions.assertEquals("APPROVED", SqlHelper.paymentStatus(testCreated))
        );
    }

    @Test
    public void creditWithApprovedCardTest() {
        Timestamp testCreated = new Timestamp(System.currentTimeMillis());

        testData = testData.withCardNumber(DataHelper.approvedCard());
        CreditPage formPage = dashBoardPage.toCreditForm();

        formPage.inputData(testData);
        formPage.sendForm();

        Assertions.assertAll(
                () -> formPage.checkSuccessNotification(),
                () -> Assertions.assertEquals("APPROVED", SqlHelper.creditStatus(testCreated))
        );
    }

    @Test
    public void paymentWithDeclinedCardTest() {
        String notificationContent = "Ошибка! Банк отказал в проведении операции.";
        Timestamp testCreated = new Timestamp(System.currentTimeMillis());

        testData = testData.withCardNumber(DataHelper.declinedCard());
        PaymentPage formPage = dashBoardPage.toPaymentForm();

        formPage.inputData(testData);
        formPage.sendForm();
        Assertions.assertAll(
                () -> formPage.checkErrorNotification(notificationContent),
                () -> Assertions.assertEquals("DECLINED", SqlHelper.paymentStatus(testCreated))
        );
    }

    @Test
    public void creditWithDeclinedCardTest() {
        String notificationContent = "Ошибка! Банк отказал в проведении операции.";
        Timestamp testCreated = new Timestamp(System.currentTimeMillis());

        testData = testData.withCardNumber(DataHelper.declinedCard());
        CreditPage formPage = dashBoardPage.toCreditForm();

        formPage.inputData(testData);
        formPage.sendForm();

        Assertions.assertAll(
                () -> formPage.checkErrorNotification(notificationContent),
                () -> Assertions.assertEquals("DECLINED", SqlHelper.creditStatus(testCreated))
        );
    }

    @Test
    public void paymentWithRandomCardTest() {
        String notificationContent = "Ошибка! Данные карты введены неверно, или нет связи с банком.";

        testData = testData.withCardNumber(DataHelper.randomCardNumber());
        PaymentPage formPage = dashBoardPage.toPaymentForm();

        formPage.inputData(testData);
        formPage.sendForm();
        formPage.checkErrorNotification(notificationContent);
    }

    @Test
    public void creditWithRandomCardTest() {
        String notificationContent = "Ошибка! Данные карты введены неверно, или нет связи с банком.";

        testData = testData.withCardNumber(DataHelper.randomCardNumber());
        CreditPage formPage = dashBoardPage.toCreditForm();

        formPage.inputData(testData);
        formPage.sendForm();
        formPage.checkErrorNotification(notificationContent);
    }

    @Test
    public void paymentWithLongCardExpiryTest() {
        Timestamp testCreated = new Timestamp(System.currentTimeMillis());

        testData = testData.withCardNumber(DataHelper.approvedCard());
        testData = testData.withCardExpiry(DataHelper.mayBeAvailableCardExpiry());
        CreditPage formPage = dashBoardPage.toCreditForm();

        formPage.inputData(testData);
        formPage.sendForm();

        Assertions.assertAll(
                () -> formPage.checkSuccessNotification(),
                () -> Assertions.assertEquals("APPROVED", SqlHelper.creditStatus(testCreated))
        );
    }

    @Test
    public void creditWithAvailableExpiredCardExpiryTest() {
        Timestamp testCreated = new Timestamp(System.currentTimeMillis());

        testData = testData.withCardNumber(DataHelper.approvedCard());
        testData = testData.withCardExpiry(DataHelper.mayBeAvailableExpiredCardExpiry());
        CreditPage formPage = dashBoardPage.toCreditForm();

        formPage.inputData(testData);
        formPage.sendForm();

        Assertions.assertAll(
                () -> formPage.checkSuccessNotification(),
                () -> Assertions.assertEquals("APPROVED", SqlHelper.creditStatus(testCreated))
        );
    }
}