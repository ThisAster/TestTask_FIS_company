package com.thisaster;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Client {
    private String firstName;
    private String middleName;
    private String lastName;
    private String birthDate;
    private Passport passport;
    private List<Credit> creditHistory;
}
