package br.gov.caixaverso.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.gov.caixaverso.dtos.CalculationMemoryDTO;
import br.gov.caixaverso.dtos.SimulationInputDTO;
import br.gov.caixaverso.dtos.SimulationRead;
import br.gov.caixaverso.exceptions.DomainValidationException;
import br.gov.caixaverso.exceptions.ResourceNotFoundException;
import br.gov.caixaverso.services.abstractions.ISimulationService;
import br.gov.caixaverso.valueobjects.MonetaryValue;
import br.gov.caixaverso.valueobjects.Percentage;
import jakarta.ws.rs.core.Response;

class SimulationResourceTest {

    @Test
    @DisplayName("Deve simular e retornar leitura")
    void shouldSimulateAndReturnRead() {
        ISimulationService service = org.mockito.Mockito.mock(ISimulationService.class);
        SimulationResource resource = new SimulationResource(service);

        SimulationInputDTO input = new SimulationInputDTO(
                MonetaryValue.from("1000"),
                Percentage.from("1"),
                2);
        SimulationRead expected = createSimulationRead(1L);
        when(service.simulate(input)).thenReturn(expected);

        Response response = resource.simulate(input);

        assertEquals(201, response.getStatus());
        assertSame(expected, response.getEntity());
        verify(service).simulate(input);
    }

    @Test
    @DisplayName("Deve lancar BadRequest quando input for nulo")
    void shouldThrowBadRequestWhenInputIsNull() {
        ISimulationService service = org.mockito.Mockito.mock(ISimulationService.class);
        SimulationResource resource = new SimulationResource(service);

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> resource.simulate(null));

        assertEquals("Dados da simulacao nao podem ser nulos", ex.getMessage());
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve listar todas as simulacoes")
    void shouldListAllSimulations() {
        ISimulationService service = org.mockito.Mockito.mock(ISimulationService.class);
        SimulationResource resource = new SimulationResource(service);

        List<SimulationRead> expected = List.of(createSimulationRead(1L), createSimulationRead(2L));
        when(service.listAll()).thenReturn(expected);

        List<SimulationRead> result = resource.listAll();

        assertEquals(expected, result);
        verify(service).listAll();
    }

    @Test
    @DisplayName("Deve retornar simulacao por id")
    void shouldGetSimulationById() {
        ISimulationService service = org.mockito.Mockito.mock(ISimulationService.class);
        SimulationResource resource = new SimulationResource(service);

        SimulationRead expected = createSimulationRead(10L);
        when(service.getById(10L)).thenReturn(expected);

        SimulationRead result = resource.getById(10L);

        assertSame(expected, result);
        verify(service).getById(10L);
    }

    @Test
    @DisplayName("Deve propagar ResourceNotFoundException no getById")
    void shouldMapNotFoundWhenGetByIdFails() {
        ISimulationService service = org.mockito.Mockito.mock(ISimulationService.class);
        SimulationResource resource = new SimulationResource(service);
        when(service.getById(99L)).thenThrow(new ResourceNotFoundException("Simulacao nao encontrada para o id: 99"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> resource.getById(99L));

        assertEquals("Simulacao nao encontrada para o id: 99", ex.getMessage());
    }

    @Test
    @DisplayName("Deve apagar simulacao por id e retornar 204")
    void shouldDeleteSimulationByIdAndReturnNoContent() {
        ISimulationService service = org.mockito.Mockito.mock(ISimulationService.class);
        SimulationResource resource = new SimulationResource(service);

        Response response = resource.deleteById(20L);

        assertEquals(204, response.getStatus());
        verify(service).deleteById(20L);
    }

    @Test
    @DisplayName("Deve propagar ResourceNotFoundException no delete")
    void shouldMapNotFoundWhenDeleteFails() {
        ISimulationService service = org.mockito.Mockito.mock(ISimulationService.class);
        SimulationResource resource = new SimulationResource(service);
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Simulacao nao encontrada para o id: 15"))
                .when(service).deleteById(15L);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> resource.deleteById(15L));

        assertEquals("Simulacao nao encontrada para o id: 15", ex.getMessage());
    }

    private static SimulationRead createSimulationRead(Long id) {
        return new SimulationRead(
                id,
            "1000.00",
            "1",
            3,
                "1030.30",
                "30.30",
                List.of(new CalculationMemoryDTO(1, "1000.00", "10.00", "1010.00")));
    }
}
