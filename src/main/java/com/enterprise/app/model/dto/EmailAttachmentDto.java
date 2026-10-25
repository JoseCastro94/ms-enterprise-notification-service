package com.enterprise.app.model.dto;

import jakarta.validation.constraints.*;
import com.enterprise.app.model.enums.EmailMediaType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailAttachmentDto {

    @NotBlank(message = "El campo fileName no puede estar vacío ni ser nulo.")
    @Size(max = 255, message = "El campo fileName no puede superar los 255 caracteres.")
    @Pattern(
        regexp = "^[^\\\\/:*?\"<>|\\r\\n]+$",
        message = "El campo fileName contiene caracteres no permitidos."
    )
    private String fileName;

    @NotNull(message = "El campo mediaType no puede estar vacío ni ser nulo.")
    private EmailMediaType mediaType;

    @NotBlank(message = "El campo contentBase64 no puede estar vacío ni ser nulo.")
    @Size(
        max = 7_000_000,
        message = "El campo contentBase64 no puede superar los 7,000,000 caracteres."
    )
    private String contentBase64;

    @JsonIgnore
    @AssertTrue(message = "El tipo de medio del adjunto no está permitido.")
    public boolean isMediaTypeAllowed() {
        return mediaType == null || mediaType != EmailMediaType.UNSUPPORTED;
    }
}