package com.enterprise.app.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailRecipientDto {

    @NotBlank(message = "El campo email no puede estar vacío ni ser nulo.")
    @Email(message = "El campo email debe tener un formato de correo electrónico válido.")
    @Size(
        max = 254,
        message = "El campo email no puede superar los 254 caracteres."
    )
    private String email;
}