package com.enterprise.app.exception;

import static com.enterprise.app.constants.ConstantsApp.ZONA_LIMA;

import com.enterprise.app.log.TransactionLogContext;
import com.enterprise.app.model.dto.errors.ErrorDetalle;
import com.enterprise.app.model.dto.errors.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {
        int status;
        String code;
        String message;
        List<ErrorDetalle> errors = List.of();

        if (exception instanceof BusinessException businessException) {
            ErrorCodes errorCode = businessException.getCode();
            status = errorCode == ErrorCodes.BAD_REQUEST
                    ? Response.Status.BAD_REQUEST.getStatusCode()
                    : Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            code = errorCode.getValue();
            message = businessException.getMessage();
        } else if (exception instanceof ConstraintViolationException validationException) {
            status = Response.Status.BAD_REQUEST.getStatusCode();
            code = ErrorCodes.BAD_REQUEST.getValue();
            message = "La solicitud contiene datos inválidos.";
            errors = validationErrors(validationException);
        } else if (exception instanceof WebApplicationException webException) {
            status = webException.getResponse().getStatus();
            code = status >= Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
                    ? ErrorCodes.ERROR.getValue()
                    : ErrorCodes.BAD_REQUEST.getValue();
            message = status == Response.Status.NOT_FOUND.getStatusCode()
                    ? "El recurso solicitado no existe."
                    : status >= Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
                            ? "Ocurrió un error interno al procesar la solicitud."
                            : "La solicitud no pudo procesarse.";
        } else {
            status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            code = ErrorCodes.ERROR.getValue();
            message = "Ocurrió un error interno al procesar la solicitud.";
        }

        String transactionId = TransactionLogContext.getTransactionId();
        if (status >= Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
            log.error("Error al procesar solicitud. transactionId={}", transactionId, exception);
        } else {
            log.warn("Solicitud rechazada. transactionId={}, status={}, code={}",
                    transactionId, status, code);
        }

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now(ZONA_LIMA))
                .status(status)
                .code(code)
                .message(message)
                .path(uriInfo == null ? null : uriInfo.getPath())
                .transactionId(transactionId)
                .errors(errors)
                .build();
        return Response.status(status).entity(body).build();
    }

    private List<ErrorDetalle> validationErrors(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(this::toErrorDetalle)
                .sorted((left, right) -> left.getField().compareTo(right.getField()))
                .toList();
    }

    private ErrorDetalle toErrorDetalle(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        String field = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
        return ErrorDetalle.builder()
                .field(field)
                .message(violation.getMessage())
                .code(ErrorCodes.BAD_REQUEST.getValue())
                .build();
    }
}
