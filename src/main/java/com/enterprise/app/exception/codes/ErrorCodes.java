package com.enterprise.app.exception.codes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes {
    BAD_REQUEST("BAD_REQUEST"),
    ERROR("ERROR");

    private final String value;
}
