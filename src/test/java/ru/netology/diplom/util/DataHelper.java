package ru.netology.diplom.util;

import com.github.javafaker.Faker;
import lombok.Value;
import lombok.With;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

@UtilityClass
public class DataHelper {

    private final Random rnd = new Random();
    private final Faker faker = new Faker();

    @Value
    public class CardInfo {
        @With
        String cardNumber;
        @With
        String cardExpiry;
        @With
        String owner;
        @With
        String code;
    }

    public CardInfo cardInfo() {
        return new CardInfo(
                randomCardNumber(),
                availableCardExpiry(),
                randomOwner(),
                randomCvcCode()
        );
    }

    public String approvedCard() {
        return "4444 4444 4444 4441";
    }

    public String declinedCard() {
        return "4444 4444 4444 4442";
    }

    public String randomCardNumber() {
        String result = null;

        for (int i = 0; i < 3; i++) {
            if (result == null || result.equals(approvedCard()) || result.equals(declinedCard())) {

                result = faker.business().creditCardNumber().replaceAll("-", " ");
            } else break;
        }
        return result;
    }

    private LocalDate beginOfYear(int plusYears) {
        return LocalDate.of(LocalDate.now().plusYears(plusYears).getYear(), Month.JANUARY, 1);
    }

    private LocalDate endOfYear(int plusYears) {
        return LocalDate.of(LocalDate.now().plusYears(plusYears).getYear(), Month.DECEMBER, 31);
    }

    public String invalidMonth() {
        String result = 13 + rnd.nextInt(86) + "";
        return result;
    }

    public String availableCardExpiry() {
        LocalDate startRange = LocalDate.now();
        Period dateRange = Period.between(startRange, endOfYear(5));
        int totalMonths = dateRange.getYears() * 12 + dateRange.getMonths();

        return startRange
                .plusMonths(rnd.nextInt(totalMonths))
                .format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    public String mayBeAvailableCardExpiry() {
        LocalDate startRange = beginOfYear(5);
        Period dateRange = Period.between(startRange, endOfYear(10));
        int totalMonths = dateRange.getYears() * 12 + dateRange.getMonths();

        return startRange
                .plusMonths(rnd.nextInt(totalMonths))
                .format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    public String mayBeAvailableExpiredCardExpiry() {
        LocalDate startRange = LocalDate.of(2022, Month.APRIL, 1);
        Period dateRange = Period.between(startRange, LocalDate.now());
        int totalMonths = dateRange.getYears() * 12 + dateRange.getMonths();

        return startRange
                .plusMonths(rnd.nextInt(totalMonths))
                .format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    public String unavailableCardExpiry() {
        return LocalDate.now()
                .plusYears(10)
                .plusMonths(1)
                .format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    public String expiredCardExpiry() {
        return "03/22";
    }

    public String randomOwner() {
        return faker.name().name();
    }

    public String randomRusOwner() {
        Faker rusFaker = new Faker(new Locale("ru"));
        return rusFaker.name().name();
    }

    public String randomCvcCode() {
        return faker.numerify("###");
    }
}