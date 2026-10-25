package com.enterprise.app.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailMediaType {
    APPLICATION_PDF("application/pdf"),
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    APPLICATION_ZIP("application/zip"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_MSWORD("application/msword"),
    APPLICATION_VND_OPENXML_WORD("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    APPLICATION_VND_MS_EXCEL("application/vnd.ms-excel"),
    APPLICATION_VND_OPENXML_SPREADSHEET(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    TEXT_PLAIN("text/plain"),
    TEXT_CSV("text/csv"),
    TEXT_HTML("text/html"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_GIF("image/gif"),
    UNSUPPORTED("unsupported");

    @JsonValue
    private final String value;

    @JsonCreator
    public static EmailMediaType fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(mediaType -> mediaType != UNSUPPORTED)
                .filter(mediaType -> mediaType.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(UNSUPPORTED);
    }
}
