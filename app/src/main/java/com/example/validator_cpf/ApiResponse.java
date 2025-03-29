package com.example.validator_cpf;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("valid")
    private boolean valid;

    @SerializedName("formatted")
    private String formatted;

    public boolean isValid() {
        return valid;
    }

    public String getFormatted() {
        return formatted;
    }
}
