package com.enterprise.app.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentType {
    HTML("HTML"),
    TEXT("TEXT"),
    UNSUPPORTED("UNSUPPORTED");

    @JsonValue
    private final String value;

    @JsonCreator
    public static ContentType fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(contentType -> contentType != UNSUPPORTED)
                .filter(contentType -> contentType.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(UNSUPPORTED);
    }
}
