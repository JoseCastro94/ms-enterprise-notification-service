package com.enterprise.app.resource;

import com.enterprise.app.business.EmailNotificationService;
import com.enterprise.app.model.dto.EmailNotificationRequest;
import com.enterprise.app.model.dto.NotificationResponse;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/notificaciones")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(
    name = "Notificaciones",
    description = "Operaciones relacionadas con el envío de notificaciones."
)
public class NotificationResource {
    private final EmailNotificationService notificationService;

    @Inject
    public NotificationResource(EmailNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @POST
    @Path("/correo")
    @Blocking
    @Operation(summary = "Enviar correo electrónico")
    public Response send(@Valid EmailNotificationRequest request) {
        notificationService.send(request);
        return Response.accepted(new NotificationResponse("Correo enviado correctamente.")).build();
    }
}
