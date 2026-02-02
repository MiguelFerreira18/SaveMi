package com.money.SaveMi.DTO.Authentication;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class StatusRequest {
    @NotNull
    public boolean authenticated;
    public StatusRequest(boolean authenticated){
        this.authenticated = authenticated;
    }
}
