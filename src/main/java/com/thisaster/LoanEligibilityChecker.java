package com.thisaster;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LoanEligibilityChecker {

    public static boolean checkLoanEligibility(String clientJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Client client = mapper.readValue(clientJson, Client.class);
            LocalDate today = LocalDate.now();

            LocalDate birthDate = LocalDate.parse(client.getBirthDate(), DateTimeFormatter.ISO_DATE_TIME);
            int age = today.getYear() - birthDate.getYear();
            if (age < 20) {
                return false;
            }


            LocalDate passportIssuedAt = LocalDate.parse(client.getPassport().getIssuedAt(), DateTimeFormatter.ISO_DATE_TIME);
            if (age > 20 && passportIssuedAt.isBefore(birthDate.plusYears(20))) {
                return false;
            }
            if (age > 45 && passportIssuedAt.isBefore(birthDate.plusYears(45))) {
                return false;
            }


            for (Credit credit : client.getCreditHistory()) {
                if (credit.getType().equals("Кредитная карта")) {
                    if (credit.getCurrentOverdueDebt() > 0 || credit.getNumberOfDaysOnOverdue() > 30) {
                        return false;
                    }
                } else {
                    if (credit.getCurrentOverdueDebt() > 0 || credit.getNumberOfDaysOnOverdue() > 60) {
                        return false;
                    }

                    int overdueCount = 0;
                    for (Credit c : client.getCreditHistory()) {
                        if (!c.getType().equals("Кредитная карта") && c.getNumberOfDaysOnOverdue() > 15) {
                            overdueCount++;
                        }
                    }
                    if (overdueCount > 2) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        // Пример 1: Клиент моложе 20 лет (проверка не пройдена)
        String clientJsonUnder20 = """
        {
            "firstName": "Алексей",
            "middleName": "Петрович",
            "lastName": "Алексеев",
            "birthDate": "2005-05-15T00:00:00.000Z",
            "passport": {
                "series": "11 22",
                "number": "123456",
                "issuedAt": "2023-06-01T00:00:00.000Z",
                "issuer": "УФМС",
                "issuerCode": "111-222"
            },
            "creditHistory": []
        }
        """;

        // Пример 2: Паспорт выдан до 20 лет или 45 лет (не проходит)
        String clientJsonPassportInvalid = """
        {
            "firstName": "Мария",
            "middleName": "Ивановна",
            "lastName": "Мартынова",
            "birthDate": "1985-01-01T00:00:00.000Z",
            "passport": {
                "series": "33 44",
                "number": "654321",
                "issuedAt": "2000-01-01T00:00:00.000Z",
                "issuer": "УФМС",
                "issuerCode": "444-555"
            },
            "creditHistory": []
        }
        """;

        // Пример 3: Нарушения в кредитной истории (не проходит)
        String clientJsonCreditIssue = """
        {
            "firstName": "Дмитрий",
            "middleName": "Анатольевич",
            "lastName": "Дмитриев",
            "birthDate": "1975-01-01T00:00:00.000Z",
            "passport": {
                "series": "22 33",
                "number": "987654",
                "issuedAt": "2015-01-01T00:00:00.000Z",
                "issuer": "УФМС",
                "issuerCode": "333-444"
            },
            "creditHistory": [
                {
                    "type": "Кредит наличными",
                    "currency": "RUB",
                    "issuedAt": "2018-06-01T00:00:00.000Z",
                    "rate": 0.15,
                    "loanSum": 50000,
                    "term": 12,
                    "repaidAt": null,
                    "currentOverdueDebt": 2000,
                    "numberOfDaysOnOverdue": 40,
                    "remainingDebt": 25000
                },
                {
                    "type": "Кредитная карта",
                    "currency": "RUB",
                    "issuedAt": "2020-05-01T00:00:00.000Z",
                    "rate": 0.24,
                    "loanSum": 20000,
                    "term": 6,
                    "repaidAt": null,
                    "currentOverdueDebt": 1000,
                    "numberOfDaysOnOverdue": 35, \s
                    "remainingDebt": 15000
                }
            ]
        }
       \s""";

        // Пример 4: Все проверки пройдены
        String clientJsonTrue = """
        {
            "firstName": "Иван",
            "middleName": "Иванович",
            "lastName": "Иванов",
            "birthDate": "1980-01-01T00:00:00.000Z",
            "passport": {
                "series": "12 34",
                "number": "123456",
                "issuedAt": "2021-01-01T00:00:00.000Z",
                "issuer": "УФМС",
                "issuerCode": "123-456"
            },
            "creditHistory": [
                {
                    "type": "Кредит наличными",
                    "currency": "RUB",
                    "issuedAt": "2020-01-01T00:00:00.000Z",
                    "rate": 0.13,
                    "loanSum": 100000,
                    "term": 12,
                    "repaidAt": "2021-01-01T00:00:00.000Z",
                    "currentOverdueDebt": 0,
                    "numberOfDaysOnOverdue": 0,
                    "remainingDebt": 0
                },
                {
                    "type": "Кредитная карта",
                    "currency": "RUB",
                    "issuedAt": "2018-01-01T00:00:00.000Z",
                    "rate": 0.24,
                    "loanSum": 30000,
                    "term": 3,
                    "repaidAt": "2019-01-01T00:00:00.000Z",
                    "currentOverdueDebt": 0,
                    "numberOfDaysOnOverdue": 10,
                    "remainingDebt": 0
                }
            ]
        }
        """;

        boolean resultUnder20 = checkLoanEligibility(clientJsonUnder20);
        System.out.println("Клиент моложе 20 лет: " + resultUnder20);

        boolean resultPassportInvalid = checkLoanEligibility(clientJsonPassportInvalid);
        System.out.println("Паспорт выдан до 20 или 45 лет: " + resultPassportInvalid);

        boolean resultCreditIssue = checkLoanEligibility(clientJsonCreditIssue);
        System.out.println("Нарушения в кредитной истории: " + resultCreditIssue);

        boolean resultValid1 = checkLoanEligibility(clientJsonTrue);
        System.out.println("Клиент прошел все проверки: " + resultValid1);
    }
}
