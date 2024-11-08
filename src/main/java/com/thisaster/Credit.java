package com.thisaster;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Credit {
    private String type;
    private String currency;
    private String issuedAt;
    private double rate;
    private double loanSum;
    private int term;
    private String repaidAt;
    private double currentOverdueDebt;
    private int numberOfDaysOnOverdue;
    private double remainingDebt;
}
