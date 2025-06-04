package ru.netology.diplom.util;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

@UtilityClass
public class DataHelper {

    private final Random RANDOM = new Random();
    private final Faker FAKER = new Faker();

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

                result = FAKER.business().creditCardNumber().replaceAll("-", " ");
            } else break;
        }
        return result;
    }

    public String incompleteCardNumber() {
        String result = randomCardNumber();
        return result.substring(0, result.length() - 1);
    }

    public String zeroCardNumber() {
        return "0000 0000 0000 0000";
    }

    public String twoRandomDigits() {
        return FAKER.numerify("##");
    }

    public String randomLetter() {
        return FAKER.letterify("?");
    }

    public char randomSpecialChar() {
        String specialChars = "~`!@#$%^&*()_-+={}[]:;'<>,.?/|";
        int index = RANDOM.nextInt(specialChars.length());

        return specialChars.charAt(index);
    }

    public String validMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
    }

    public String validYear() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
    }

    public String invalidMonth() {
        String result = 13 + RANDOM.nextInt(86) + "/" + LocalDate.now().getYear();
        return result;
    }

    public String zeroMonth() {
        return "00";
    }

    public String currentDateCardExpiry() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    public String expiredCardExpiry() {
        return "03/22";
    }

    public String minCardExpiry() {
        return "04/22";
    }

    public String maxCardExpiry() {
        return LocalDate.now().plusYears(20).format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    public String unavailableCardExpiry() {
        return LocalDate.now().plusYears(20).plusMonths(1).format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    public String randomOwner() {
        String result = FAKER.name().name();

        if (result.length() > 25) {
            return result.substring(0, 25);
        } else {
            return result;
        }
    }

    public String minOwnerLength() {
        return randomLetter() + randomLetter();
    }

    public String maxOwnerLength() {
        StringBuilder result = new StringBuilder(randomOwner());

        for (int i = 0; i < 3; i++) {
            if (result.length() < 27) {
                result.append(result);
            } else break;
        }
        return result.substring(0, 27);
    }

    public String unavailableOwnerLength() {
        return maxOwnerLength() + randomLetter();
    }

    public String randomRusOwner() {
        Faker rusFaker = new Faker(new Locale("ru"));
        String result = rusFaker.name().name();

        if (result.length() > 27) {
            return result.substring(0, 27);
        } else {
            return result;
        }
    }

    public String randomCvcCode() {
        return FAKER.numerify("###");
    }

    public String zeroCvcCode() {
        return "000";
    }
}