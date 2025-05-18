package ru.netology.diplom.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ru.netology.diplom.util.DataHelper;

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

    public void inputData(DataHelper.CardInfo cardInfo) {
        cardNumberInput.find("input").setValue(cardInfo.getCardNumber());
        monthInput.find("input").setValue(cardInfo.getCardExpiry().substring(0, 2));
        yearInput.find("input").setValue(cardInfo.getCardExpiry().substring(3));
        ownerInput.find("input").setValue(cardInfo.getOwner());
        cvcInput.find("input").setValue(cardInfo.getCode());
    }

    public void inputDataWithInvalidMonth(DataHelper.CardInfo cardInfo) {
        cardNumberInput.find("input").setValue(cardInfo.getCardNumber());
        monthInput.find("input").setValue(DataHelper.invalidMonth());
        yearInput.find("input").setValue(cardInfo.getCardExpiry().substring(3));
        ownerInput.find("input").setValue(cardInfo.getOwner());
        cvcInput.find("input").setValue(cardInfo.getCode());
    }

    public void sendForm() {
        sendButton.click();
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

    public void checkInvalidMonthMessage() {
        monthInput.find(".input-group__input-case_invalid .input__sub").shouldHave(Condition.exactText("Неверно указан срок действия карты"));
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