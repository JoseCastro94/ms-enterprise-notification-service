package com.enterprise.app.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class EmailNotificationResourceTest {
    private static final String ENDPOINT = "/api/v1/notificaciones/correo";

    @Test
    void enviaCorreoConDestinatariosYCuerpoHtml() {
        given()
                .header("X-Transaction-Id", "notif-test-001")
                .contentType("application/json")
                .body("""
                        {
                          "recipients": {
                            "to": [{"email": "usuario@correo.com"}],
                            "cc": [{"email": "copia@correo.com"}],
                            "bcc": [{"email": "oculto@correo.com"}]
                          },
                          "subject": "Mensaje de prueba",
                          "content": "<h1>Hola Mundo</h1>",
                          "contentType": "HTML",
                          "attachments": [{
                            "fileName": "archivo.pdf",
                            "mediaType": "application/pdf",
                            "contentBase64": "JVBERi0xLjQK"
                          }]
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(202)
                .header("X-Transaction-Id", equalTo("notif-test-001"))
                .body("message", equalTo("Correo enviado correctamente."));
    }

    @Test
    void rechazaDestinatarioConCorreoInvalido() {
        given()
                .header("X-Transaction-Id", "notif-test-002")
                .contentType("application/json")
                .body("""
                        {
                          "recipients": {"to": [{"email": "no-es-correo"}]},
                          "subject": "Mensaje",
                          "content": "Hola",
                          "contentType": "TEXT"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("message", equalTo("La solicitud contiene datos inválidos."))
                .body("errors[0].field", equalTo("email"))
                .body("errors[0].message", equalTo("El campo email debe tener un formato de correo electrónico válido."))
                .body("errors[0].code", equalTo("BAD_REQUEST"));
    }

    @Test
    void rechazaSolicitudSinDestinatarios() {
        given()
                .header("X-Transaction-Id", "notif-test-003")
                .contentType("application/json")
                .body("""
                        {
                          "recipients": {},
                          "subject": "Mensaje",
                          "content": "Hola",
                          "contentType": "TEXT"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("message", equalTo("La solicitud contiene datos inválidos."))
                .body("errors[0].field", equalTo("to"))
                .body("errors[0].message", equalTo("Debe indicar al menos un destinatario en to."));
    }

    @Test
    void permiteCcYBccVaciosSiToTieneAlMenosUnCorreo() {
        given()
                .header("X-Transaction-Id", "notif-test-006")
                .contentType("application/json")
                .body("""
                        {
                          "recipients": {
                            "to": [{"email": "usuario@correo.com"}],
                            "cc": [],
                            "bcc": []
                          },
                          "subject": "Mensaje",
                          "content": "Hola",
                          "contentType": "TEXT"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(202)
                .body("message", equalTo("Correo enviado correctamente."));
    }

    @Test
    void rechazaCcOBccConCorreoInvalido() {
        given()
                .header("X-Transaction-Id", "notif-test-007")
                .contentType("application/json")
                .body("""
                        {
                          "recipients": {
                            "to": [{"email": "usuario@correo.com"}],
                            "cc": [{"email": "correo-invalido"}],
                            "bcc": []
                          },
                          "subject": "Mensaje",
                          "content": "Hola",
                          "contentType": "TEXT"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("message", equalTo("La solicitud contiene datos inválidos."))
                .body("errors[0].field", equalTo("email"))
                .body("errors[0].message", equalTo("El campo email debe tener un formato de correo electrónico válido."));
    }

    @Test
    void rechazaAdjuntoConBase64Invalido() {
        given()
                .header("X-Transaction-Id", "notif-test-004")
                .contentType("application/json")
                .body("""
                        {
                          "recipients": {"to": [{"email": "usuario@correo.com"}]},
                          "subject": "Mensaje",
                          "content": "Hola",
                          "contentType": "TEXT",
                          "attachments": [{
                            "fileName": "archivo.pdf",
                            "mediaType": "application/pdf",
                            "contentBase64": "no-es-base64!"
                          }]
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("message", equalTo("El contenido del adjunto debe ser Base64 válido."))
                .body("code", equalTo("BAD_REQUEST"))
                .body("errors", org.hamcrest.Matchers.empty());
    }

    @Test
    void rechazaSolicitudSinHeaderDeTrazabilidad() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "recipients": {"to": [{"email": "usuario@correo.com"}]},
                          "subject": "Mensaje",
                          "content": "Hola",
                          "contentType": "TEXT"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("message", equalTo("El header X-Transaction-Id es obligatorio."));
    }

    @Test
    void rechazaTipoDeMedioNoIncluidoEnEnum() {
        given()
                .header("X-Transaction-Id", "notif-test-005")
                .contentType("application/json")
                .body("""
                        {
                          "recipients": {"to": [{"email": "usuario@correo.com"}]},
                          "subject": "Mensaje",
                          "content": "Hola",
                          "contentType": "TEXT",
                          "attachments": [{
                            "fileName": "archivo.bin",
                            "mediaType": "application/x-tipo-desconocido",
                            "contentBase64": "SG9sYQ=="
                          }]
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("message", equalTo("La solicitud contiene datos inválidos."))
                .body("errors[0].field", equalTo("mediaTypeAllowed"))
                .body("errors[0].message", equalTo("El tipo de medio del adjunto no está permitido."))
                .body("transactionId", equalTo("notif-test-005"));
    }

    @Test
    void aceptaContentTypeSinDistinguirMayusculas() {
        given()
                .header("X-Transaction-Id", "notif-test-009")
                .contentType("application/json")
                .body("""
                        {
                          "recipients": {"to": [{"email": "usuario@correo.com"}]},
                          "subject": "Mensaje",
                          "content": "<p>Hola</p>",
                          "contentType": "html"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(202)
                .body("message", equalTo("Correo enviado correctamente."));
    }

    @Test
    void rechazaContentTypeDesconocidoConErrorJson() {
        given()
                .header("X-Transaction-Id", "notif-test-010")
                .contentType("application/json")
                .body("""
                        {
                          "recipients": {"to": [{"email": "usuario@correo.com"}]},
                          "subject": "Mensaje",
                          "content": "Hola",
                          "contentType": "MARKDOWN"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("message", equalTo("La solicitud contiene datos inválidos."))
                .body("errors[0].field", equalTo("contentTypeAllowed"))
                .body("errors[0].message", equalTo("El tipo de contenido permitido es HTML o TEXT."));
    }
}
