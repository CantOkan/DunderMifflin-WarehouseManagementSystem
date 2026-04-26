package com.canok.whmanagement.dto;

import com.canok.whmanagement.security.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long accountId;
    private String username;
    private AccountType accountType;
}