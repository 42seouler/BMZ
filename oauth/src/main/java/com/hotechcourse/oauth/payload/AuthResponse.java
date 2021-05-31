package com.hotechcourse.oauth.payload;

import lombok.Builder;
import lombok.Data;

@Data
public class AuthResponse {
    private String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;

    @Builder
    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
