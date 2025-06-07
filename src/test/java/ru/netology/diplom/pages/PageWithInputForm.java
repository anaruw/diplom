package ru.netology.diplom.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

public class PageWithInputForm {

    private final SelenideElement cardNumberInput = $(By.xpath("//*[text() = 'Номер карты']/.."));
    private final SelenideElement monthInput = $(By.xpath("//*[text() = 'Месяц']/.."));
    private final SelenideElement yearInput = $(By.xpath("//*[text() = 'Год']/.."));
    private final SelenideElement ownerInput = $(By.xpath("//*[text() = 'Владелец']/.."));
    private final SelenideElement cvcInput = $(By.xpath("//*[text() = 'CVC/CVV']/.."));
    private final SelenideElement sendButton = $(By.xpath("//*[text() = 'Продолжить']/../.."));
    private final SelenideElement successNotification = $(".notification_status_ok .notification__content");
    private final SelenideElement errorNotification = $(".notification_status_error .notification__content");

    public void setCardNumberInput(String cardNumber) {
        cardNumberInput.find("input").setValue(cardNumber);
    }

    public void setMonthInput(String month) {
        monthInput.find("input").setValue(month);
    }

    public void setYearInput(String year) {
        yearInput.find("input").setValue(year);
    }

    public void setCardExpiryInput(String cardExpiry) {
        monthInput.find("input").setValue(cardExpiry.substring(0, cardExpiry.indexOf("/")));
        yearInput.find("input").setValue(cardExpiry.substring(cardExpiry.indexOf("/") + 1));
    }

    public void setOwnerInput(String owner) {
        ownerInput.find("input").setValue(owner);
    }
    public void setCvcInput(String cvcCode) {
        cvcInput.find("input").setValue(cvcCode);
    }

    public void sendForm() {
        sendButton.click();
    }

    public void checkCardNumberInput(String expectedValue) {
        cardNumberInput.find("input").shouldHave(Condition.value(expectedValue));
    }

    public void checkOwnerInput(String expectedOwner) {
        ownerInput.find("input").shouldHave(Condition.value(expectedOwner));
    }

    public void checkCvcInput(String expectedCvcCode) {
        cvcInput.find("input").shouldHave(Condition.value(expectedCvcCode));
    }

    public void checkSuccessNotification() {
        successNotification.shouldBe(Condition.visible, Duration.ofSeconds(15));
        successNotification.shouldHave(Condition.exactText("Операция одобрена Банком."));
    }

    public void checkErrorNotification(String notificationContent) {
        errorNotification.shouldBe(Condition.visible, Duration.ofSeconds(15));
        errorNotification.shouldHave(Condition.exactText(notificationContent));
    }

    public void checkInvalidCardNumberMessage() {
        cardNumberInput.find(".input_invalid .input__sub").shouldHave(Condition.exactText("Неверный формат"));
    }

    public void checkInvalidMonthMessage(String messageContent) {
        monthInput.find(".input-group__input-case_invalid .input__sub").shouldHave(Condition.exactText(messageContent));
    }

    public void checkInvalidYearMessage(String messageContent) {
        yearInput.find(".input-group__input-case_invalid .input__sub").shouldHave(Condition.exactText(messageContent));
    }

    public void checkInvalidOwnerMessage(String messageContent) {
        ownerInput.find(".input-group__input-case_invalid .input__sub").shouldHave(Condition.exactText(messageContent));
    }

    public void checkInvalidCvcMessage() {
        cvcInput.find(".input-group__input-case_invalid .input__sub").shouldHave(Condition.exactText("Неверный формат"));
    }
}