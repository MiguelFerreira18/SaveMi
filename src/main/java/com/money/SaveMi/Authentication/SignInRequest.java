package com.money.SaveMi.Authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignInRequest {

    @NotNull
    @Email(message = "Invalid email format")
    String email;
    @NotNull
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,128}$", message = "Password must contain at least one letter and one number")
    String password;

    public @NotNull @Email(message = "Invalid email format") String getEmail() {
        return email;
    }

    public @NotNull @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,128}$", message = "Password must contain at least one letter and one number") String getPassword() {
        return password;
    }

}
