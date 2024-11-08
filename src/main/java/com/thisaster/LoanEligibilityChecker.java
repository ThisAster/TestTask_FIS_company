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
            if (age < 20) return false;

            LocalDate passportIssuedAt = LocalDate.parse(client.getPassport().getIssuedAt(), DateTimeFormatter.ISO_DATE_TIME);
            if (age > 20 && passportIssuedAt.isBefore(birthDate.plusYears(20))) return false;
            if (age > 45 && passportIssuedAt.isBefore(birthDate.plusYears(45))) return false;

            for (Credit credit : client.getCreditHistory()) {
                if (credit.getType().equals("Кредитная карта")) {
                    if (credit.getCurrentOverdueDebt() > 0 || credit.getNumberOfDaysOnOverdue() > 30) {
                        return false;
                    }
                } else {
                    if (credit.getCurrentOverdueDebt() > 0 || credit.getNumberOfDaysOnOverdue() > 60 || credit.getNumberOfDaysOnOverdue() > 15) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {

            return false;
        }
    }

    public static void main(String[] args) {
        String clientJsonFalse = """
        {
            "firstName": "Иван",
            "middleName": "Иванович",
            "lastName": "Иванов",
            "birthDate": "1969-12-31T21:00:00.000Z",
            "passport": {
                "series": "12 34",
                "number": "123456",
                "issuedAt": "2023-03-11T21:00:00.000Z",
                "issuer": "УФМС",
                "issuerCode": "123-456"
            },
            "creditHistory": [
                {
                    "type": "Кредит наличными",
                    "currency": "RUB",
                    "issuedAt": "2003-02-27T21:00:00.000Z",
                    "rate": 0.13,
                    "loanSum": 100000,
                    "term": 12,
                    "repaidAt": "2004-02-27T21:00:00.000Z",
                    "currentOverdueDebt": 0,
                    "numberOfDaysOnOverdue": 0,
                    "remainingDebt": 0
                },
                {
                    "type": "Кредитная карта",
                    "currency": "RUB",
                    "issuedAt": "2009-03-27T21:00:00.000Z",
                    "rate": 0.24,
                    "loanSum": 30000,
                    "term": 3,
                    "repaidAt": "2009-06-29T20:00:00.000Z",
                    "currentOverdueDebt": 0,
                    "numberOfDaysOnOverdue": 2,
                    "remainingDebt": 0
                },
                {
                    "type": "Кредит наличными",
                    "currency": "RUB",
                    "issuedAt": "2009-02-27T21:00:00.000Z",
                    "rate": 0.09,
                    "loanSum": 200000,
                    "term": 24,
                    "repaidAt": "2011-03-02T21:00:00.000Z",
                    "currentOverdueDebt": 0,
                    "numberOfDaysOnOverdue": 3,
                    "remainingDebt": 0
                },
                {
                    "type": "Кредит наличными",
                    "currency": "RUB",
                    "issuedAt": "2024-05-15T21:00:00.000Z",
                    "rate": 0.13,
                    "loanSum": 200000,
                    "term": 36,
                    "repaidAt": null,
                    "currentOverdueDebt": 10379,
                    "numberOfDaysOnOverdue": 15,
                    "remainingDebt": 110000
                }
            ]
        }
        """;

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


        boolean resultTrue = checkLoanEligibility(clientJsonTrue);
        System.out.println("Клиент прошел все проверки: " + resultTrue);

        boolean resultFalse = checkLoanEligibility(clientJsonFalse);
        System.out.println("Клиент прошел все проверки: " + resultFalse);
    }
}
