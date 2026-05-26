package br.gov.caixaverso.resources.abstractions;

import java.util.List;

import br.gov.caixaverso.dtos.SimulationInputDTO;
import br.gov.caixaverso.dtos.SimulationRead;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulacoes", description = "Operacoes de simulacao financeira")
public interface ISimulationEndpoint {

    @POST
    @Operation(summary = "Cria uma nova simulacao")
    @APIResponse(responseCode = "201", description = "Simulacao criada com sucesso", content = @Content(schema = @Schema(implementation = SimulationRead.class)))
    @APIResponse(responseCode = "400", description = "Dados de entrada invalidos")
    Response simulate(
            @RequestBody(required = true, description = "Parametros de entrada da simulacao")
            SimulationInputDTO input);

    @GET
    @Operation(summary = "Lista todas as simulacoes")
    @APIResponse(responseCode = "200", description = "Lista de simulacoes")
    List<SimulationRead> listAll();

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca simulacao por id")
    @APIResponse(responseCode = "200", description = "Simulacao encontrada", content = @Content(schema = @Schema(implementation = SimulationRead.class)))
    @APIResponse(responseCode = "404", description = "Simulacao nao encontrada")
    SimulationRead getById(@PathParam("id") Long id);

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Apaga simulacao por id")
    @APIResponse(responseCode = "204", description = "Simulacao apagada com sucesso")
    @APIResponse(responseCode = "404", description = "Simulacao nao encontrada")
    Response deleteById(@PathParam("id") Long id);
}
