package in.alekproduction.moneymanager.dto;

import lombok.Data;

@Data
public class AuthDto {
    private String email;
    private String password;
    private String token;
}
