package com.hotechcourse.oauth.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class TokenRefreshRequest {

    @ApiModelProperty(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjIyMzQ2MTEyLCJleHAiOjE2MjMyMTAxMTJ9.xxPzkr-2x9C_JfB8z9PbQo0XEBqA_Km9XICj0OLLHkE")
    private String refreshToken;

}
