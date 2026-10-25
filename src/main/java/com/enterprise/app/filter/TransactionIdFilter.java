package com.enterprise.app.filter;

import static com.enterprise.app.constants.ConstantsApp.HEADER_TRANSACTION_ID;
import static com.enterprise.app.constants.ConstantsApp.ID_TRANSACTION;

import com.enterprise.app.exception.codes.ErrorCodes;
import com.enterprise.app.constants.ConstantsApp;
import com.enterprise.app.context.TransactionLogContext;
import com.enterprise.app.exception.responses.ErrorResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

@Provider
public class TransactionIdFilter implements ContainerRequestFilter, ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String transactionId = requestContext.getHeaderString(HEADER_TRANSACTION_ID);
        if (transactionId == null || transactionId.isBlank()) {
            requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ErrorResponse.builder()
                            .timestamp(OffsetDateTime.now(ConstantsApp.ZONA_LIMA))
                            .status(Response.Status.BAD_REQUEST.getStatusCode())
                            .code(ErrorCodes.BAD_REQUEST.getValue())
                            .message("El header X-Transaction-Id es obligatorio.")
                            .path(requestContext.getUriInfo().getPath())
                            .errors(List.of())
                            .build())
                    .build());
            return;
        }

        requestContext.setProperty(ID_TRANSACTION, transactionId);
        TransactionLogContext.setTransactionId(transactionId);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        Object transactionId = requestContext.getProperty(ID_TRANSACTION);
        if (transactionId != null) {
            responseContext.getHeaders().putSingle(HEADER_TRANSACTION_ID, transactionId.toString());
        }
        TransactionLogContext.clear();
    }
}
