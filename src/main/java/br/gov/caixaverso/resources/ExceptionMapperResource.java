package br.gov.caixaverso.resources;

import br.gov.caixaverso.dtos.ErrorResponseDTO;
import br.gov.caixaverso.exceptions.DomainValidationException;
import br.gov.caixaverso.exceptions.MonetaryValueException;
import br.gov.caixaverso.exceptions.PercentageException;
import br.gov.caixaverso.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class ExceptionMapperResource implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof ResourceNotFoundException) {
            return buildResponse(Response.Status.NOT_FOUND, exception.getMessage());
        }

        if (exception instanceof DomainValidationException
                || exception instanceof MonetaryValueException
                || exception instanceof PercentageException) {
            return buildResponse(Response.Status.BAD_REQUEST, exception.getMessage());
        }

        return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
    }

    private Response buildResponse(Response.Status status, String message) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponseDTO(message))
                .build();
    }
}
