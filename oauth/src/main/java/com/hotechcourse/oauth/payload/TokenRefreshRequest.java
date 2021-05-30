package com.hotechcourse.oauth.payload;

import lombok.Data;

@Data
public class TokenRefreshRequest {

    private String refreshToken;

}
