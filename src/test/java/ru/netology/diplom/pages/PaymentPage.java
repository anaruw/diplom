package ru.netology.diplom.pages;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class PaymentPage extends PageWithInputForm {

    public PaymentPage() {
        $(By.xpath("//*[text() = 'Оплата по карте']")).shouldBe(Condition.visible);
    }
}