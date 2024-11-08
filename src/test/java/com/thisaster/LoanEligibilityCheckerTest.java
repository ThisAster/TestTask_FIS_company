package com.thisaster;

import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

class LoanEligibilityCheckerTest {

    @Test
    void testClientIsTooYoung() {
        String clientJson = """
            {
                "firstName": "Иван",
                "birthDate": "2005-01-01T00:00:00.000Z",
                "passport": {
                    "issuedAt": "2022-01-01T00:00:00.000Z"
                },
                "creditHistory": []
            }
        """;
        boolean result = LoanEligibilityChecker.checkLoanEligibility(clientJson);
        assertFalse("Client should be denied for being underage", result);
    }

    @Test
    void testPassportIssuedTooEarly() {
        String clientJson = """
            {
                "firstName": "Иван",
                "birthDate": "1980-01-01T00:00:00.000Z",
                "passport": {
                    "issuedAt": "1995-01-01T00:00:00.000Z"
                },
                "creditHistory": []
            }
        """;
        boolean result = LoanEligibilityChecker.checkLoanEligibility(clientJson);
        assertFalse("Client should be denied due to passport issued too early", result);
    }

    @Test
    void testValidClientEligibility() {
        String clientJson = """
            {
                "firstName": "Иван",
                "birthDate": "1980-01-01T00:00:00.000Z",
                "passport": {
                    "issuedAt": "2021-01-01T00:00:00.000Z"
                },
                "creditHistory": [
                    {
                        "type": "Кредит наличными",
                        "currentOverdueDebt": 0,
                        "numberOfDaysOnOverdue": 0
                    }
                ]
            }
        """;
        boolean result = LoanEligibilityChecker.checkLoanEligibility(clientJson);
        assertTrue("Client should be eligible for a loan", result);
    }

    @Test
    void testOverdueDebtInCreditHistory() {
        String clientJson = """
            {
                "firstName": "Иван",
                "birthDate": "1980-01-01T00:00:00.000Z",
                "passport": {
                    "issuedAt": "2021-01-01T00:00:00.000Z"
                },
                "creditHistory": [
                    {
                        "type": "Кредит наличными",
                        "currentOverdueDebt": 10000,
                        "numberOfDaysOnOverdue": 5
                    }
                ]
            }
        """;
        boolean result = LoanEligibilityChecker.checkLoanEligibility(clientJson);
        assertFalse("Client should be denied due to overdue debt", result);
    }

    @Test
    void testCreditCardWithTooLongOverdue() {
        String clientJson = """
            {
                "firstName": "Иван",
                "birthDate": "1980-01-01T00:00:00.000Z",
                "passport": {
                    "issuedAt": "2021-01-01T00:00:00.000Z"
                },
                "creditHistory": [
                    {
                        "type": "Кредитная карта",
                        "currentOverdueDebt": 0,
                        "numberOfDaysOnOverdue": 35
                    }
                ]
            }
        """;
        boolean result = LoanEligibilityChecker.checkLoanEligibility(clientJson);
        assertFalse("Client should be denied due to credit card overdue debt exceeding 30 days", result);
    }

    @Test
    void testMultipleCreditsWithOverdue() {
        String clientJson = """
            {
                "firstName": "Иван",
                "birthDate": "1980-01-01T00:00:00.000Z",
                "passport": {
                    "issuedAt": "2021-01-01T00:00:00.000Z"
                },
                "creditHistory": [
                    {
                        "type": "Кредит наличными",
                        "currentOverdueDebt": 0,
                        "numberOfDaysOnOverdue": 20
                    },
                    {
                        "type": "Кредит наличными",
                        "currentOverdueDebt": 0,
                        "numberOfDaysOnOverdue": 18
                    },
                    {
                        "type": "Кредит наличными",
                        "currentOverdueDebt": 0,
                        "numberOfDaysOnOverdue": 16
                    }
                ]
            }
        """;
        boolean result = LoanEligibilityChecker.checkLoanEligibility(clientJson);
        assertFalse("Client should be denied due to multiple credits with overdue debt over 15 days", result);
    }

    @Test
    void testMultipleCreditsWithOverdueTrue() {
        String clientJson = """
            {
                "firstName": "Иван",
                "birthDate": "1980-01-01T00:00:00.000Z",
                "passport": {
                    "issuedAt": "2021-01-01T00:00:00.000Z"
                },
                "creditHistory": [
                    {
                        "type": "Кредит наличными",
                        "currentOverdueDebt": 0,
                        "numberOfDaysOnOverdue": 20
                    },
                    {
                        "type": "Кредит наличными",
                        "currentOverdueDebt": 0,
                        "numberOfDaysOnOverdue": 14
                    },
                    {
                        "type": "Кредит наличными",
                        "currentOverdueDebt": 0,
                        "numberOfDaysOnOverdue": 14
                    }
                ]
            }
        """;
        boolean result = LoanEligibilityChecker.checkLoanEligibility(clientJson);
        assertTrue("Client should be denied due to multiple credits with overdue debt over 15 days", result);
    }

    @Test
    void testClientWithValidCreditHistory() {
        String clientJson = """
            {
                "firstName": "Иван",
                "birthDate": "1980-01-01T00:00:00.000Z",
                "passport": {
                    "issuedAt": "2021-01-01T00:00:00.000Z"
                },
                "creditHistory": [
                    {
                        "type": "Кредит наличными",
                        "currentOverdueDebt": 0,
                        "numberOfDaysOnOverdue": 60
                    },
                    {
                        "type": "Кредитная карта",
                        "currentOverdueDebt": 0,
                        "numberOfDaysOnOverdue": 30
                    }
                ]
            }
        """;
        boolean result = LoanEligibilityChecker.checkLoanEligibility(clientJson);
        assertTrue("Client should be eligible due to clean credit history", result);
    }
}
