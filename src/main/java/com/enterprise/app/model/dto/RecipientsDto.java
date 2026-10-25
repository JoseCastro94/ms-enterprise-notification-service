package com.enterprise.app.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecipientsDto {
    @NotEmpty(message = "Debe indicar al menos un destinatario en to.")
    @Size(max = 50)
    private List<@NotNull @Valid EmailRecipientDto> to;

    @Size(max = 50)
    private List<@NotNull @Valid EmailRecipientDto> cc;

    @Size(max = 50)
    private List<@NotNull @Valid EmailRecipientDto> bcc;
}
