package com.thisaster;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Passport {
    private String series;
    private String number;
    private String issuedAt;
    private String issuer;
    private String issuerCode;
}
