# ms-enterprise-notification-service

Microservicio Quarkus dedicado exclusivamente al envío de notificaciones por correo, con soporte para destinatarios `to`, `cc` y `bcc`, contenido HTML o texto y hasta cinco adjuntos Base64.

## Endpoint

`POST /api/v1/notificaciones/correo` requiere el header `X-Transaction-Id` para mantener la trazabilidad y recibe JSON:

```json
{
  "recipients": {
    "to": [{ "email": "usuario@correo.com" }],
    "cc": [{ "email": "copia@correo.com" }],
    "bcc": [{ "email": "oculto@correo.com" }]
  },
  "subject": "Mensaje de prueba",
  "content": "<h1>Hola Mundo</h1>",
  "contentType": "HTML",
  "attachments": [{
    "fileName": "archivo.pdf",
    "mediaType": "application/pdf",
    "contentBase64": "JVBERi0xLjQK..."
  }]
}
```

Se responde con `202 Accepted` cuando el proveedor SMTP acepta el correo. `contentType` acepta `HTML` o `TEXT` sin distinguir mayúsculas/minúsculas. `recipients.to` es obligatorio y debe incluir al menos una dirección; `cc` y `bcc` son opcionales y pueden omitirse o enviarse como listas vacías. Las direcciones en cualquiera de las listas se validan con Jakarta Validation. Los errores devuelven JSON uniforme con `timestamp`, `status`, `code`, `message`, `path`, `transactionId` y `errors`; cada elemento de `errors` incluye `field`, `message` y `code`. `BusinessException` representa errores funcionales, por ejemplo contenido Base64 inválido o adjuntos que exceden el tamaño permitido. El mismo `X-Transaction-Id` se devuelve en el header de respuesta y se agrega al contexto de logging. Los tipos de adjunto se validan mediante un enum de MIME permitidos (`application/pdf`, `image/png`, `text/csv`, entre otros). El servicio limita cada adjunto a 5 MiB por defecto.

## Configuración SMTP

Configura `SMTP_HOST`, `SMTP_PORT`, `SMTP_FROM`, `SMTP_USERNAME` y `SMTP_PASSWORD`. `SMTP_START_TLS` acepta `DISABLED`, `OPTIONAL` o `REQUIRED`. Se recomienda inyectar credenciales mediante el entorno o un gestor de secretos, no guardarlas en el repositorio.

El límite de tamaño de cada archivo se puede ajustar con `NOTIFICATION_MAX_ATTACHMENT_SIZE_BYTES`. El límite HTTP total de la solicitud es 10 MiB.

En pruebas el mailer usa el modo mock, así que no se requiere un servidor SMTP:

```powershell
.\mvnw.cmd test
```

Para iniciar en desarrollo:

```powershell
$env:SMTP_HOST = "localhost"
$env:SMTP_PORT = "1025"
.\mvnw.cmd quarkus:dev
```

La documentación OpenAPI está disponible en `/q/openapi` y Swagger UI en `/q/swagger-ui`.
