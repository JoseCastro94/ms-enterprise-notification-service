package com.enterprise.app.model.dto;

import com.enterprise.app.model.enums.ContentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailNotificationRequest {

    @NotNull(message = "El campo recipients no puede ser nulo.")
    @Valid
    private RecipientsDto recipients;

    @NotBlank(message = "El campo subject no puede estar vacío ni ser nulo.")
    @Size(
        max = 998,
        message = "El campo subject no puede superar los 998 caracteres."
    )
    @Pattern(
        regexp = "^[^\\r\\n]*$",
        message = "El campo subject no puede contener saltos de línea."
    )
    private String subject;

    @NotBlank(message = "El campo content no puede estar vacío ni ser nulo.")
    @Size(
        max = 200_000,
        message = "El campo content no puede superar los 200,000 caracteres."
    )
    private String content;

    @NotNull(message = "El campo contentType no puede estar vacío ni ser nulo.")
    private ContentType contentType;

    @Size(
        max = 5,
        message = "Solo se permiten hasta 5 archivos adjuntos."
    )
    private List<@Valid EmailAttachmentDto> attachments;

    @JsonIgnore
    @AssertTrue(message = "El tipo de contenido permitido es HTML o TEXT.")
    public boolean isContentTypeAllowed() {
        return contentType == null || contentType != ContentType.UNSUPPORTED;
    }
}