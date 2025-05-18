package ru.netology.diplom.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class DashBoardPage {

    private final SelenideElement creditButton = $(By.xpath("//*[text() = 'Купить в кредит']/../.."));
    private final SelenideElement paymentButton = $(By.xpath("//*[text() = 'Купить']/../.."));

    public DashBoardPage() {
        $(By.xpath("//*[text() = 'Путешествие дня']")).shouldBe(Condition.visible);
    }

    public CreditPage toCreditForm() {
        creditButton.click();
        return new CreditPage();
    }

    public PaymentPage toPaymentForm() {
        paymentButton.click();
        return new PaymentPage();
    }
}