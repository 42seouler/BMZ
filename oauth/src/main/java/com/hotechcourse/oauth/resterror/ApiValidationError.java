package com.hotechcourse.oauth.resterror;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ApiValidationError extends ApiSubError {

    private String object;
    private String field;
    private Object rejectedValude;
    private String message;

    public ApiValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }
}
