package br.gov.caixaverso.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.gov.caixaverso.dtos.SimulationInput;
import br.gov.caixaverso.entities.Simulation;
import br.gov.caixaverso.repositories.abstractions.ISimulationRepository;
import br.gov.caixaverso.valueobjects.MonetaryValue;
import br.gov.caixaverso.valueobjects.Percentage;

class SimulationServiceTest {

    @Test
    @DisplayName("Deve simular juros compostos mes a mes")
    void shouldSimulateCompoundInterestMonthByMonth() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        doNothing().when(repository).persist(any(Simulation.class));

        SimulationService service = new SimulationService(repository);
        SimulationInput input = new SimulationInput(
                MonetaryValue.from("1000.00"),
                Percentage.from("1"),
                3);

        Simulation result = service.simulate(input);

        assertEquals(3, result.getCalculationMemories().size());

        assertAmountEquals("1000.00", result.getCalculationMemories().get(0).getInitialBalance().getValue());
        assertAmountEquals("10.00", result.getCalculationMemories().get(0).getInterestAmount().getValue());
        assertAmountEquals("1010.00", result.getCalculationMemories().get(0).getFinalBalance().getValue());

        assertAmountEquals("1010.00", result.getCalculationMemories().get(1).getInitialBalance().getValue());
        assertAmountEquals("10.10", result.getCalculationMemories().get(1).getInterestAmount().getValue());
        assertAmountEquals("1020.10", result.getCalculationMemories().get(1).getFinalBalance().getValue());

        assertAmountEquals("1020.10", result.getCalculationMemories().get(2).getInitialBalance().getValue());
        assertAmountEquals("10.201", result.getCalculationMemories().get(2).getInterestAmount().getValue());
        assertAmountEquals("1030.301", result.getCalculationMemories().get(2).getFinalBalance().getValue());

        assertAmountEquals("30.301", result.getTotalInterestAmount().getValue());
        assertAmountEquals("1030.301", result.getTotalFinalAmount().getValue());

        verify(repository).persist(any(Simulation.class));
    }

    @Test
    @DisplayName("Deve lancar excecao quando input for nulo")
    void shouldThrowWhenInputIsNull() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        SimulationService service = new SimulationService(repository);

        NullPointerException ex = assertThrows(NullPointerException.class, () -> service.simulate(null));

        assertEquals("Dados da simulacao nao podem ser nulos", ex.getMessage());
    }

    private static void assertAmountEquals(String expected, BigDecimal actual) {
        assertTrue(new BigDecimal(expected).compareTo(actual) == 0,
                () -> "expected: <" + expected + "> but was: <" + actual + ">");
    }
}
