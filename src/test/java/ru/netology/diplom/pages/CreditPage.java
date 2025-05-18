package ru.netology.diplom.pages;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class CreditPage extends PageWithInputForm {

    public CreditPage() {
        $(By.xpath("//*[text() = 'Кредит по данным карты']")).shouldBe(Condition.visible);
    }
}