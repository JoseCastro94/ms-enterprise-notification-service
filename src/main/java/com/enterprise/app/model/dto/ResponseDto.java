package com.enterprise.app.model.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto {
    private OffsetDateTime timestamp;
    private int status;
    private String message;
}
