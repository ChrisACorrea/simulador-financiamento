package br.gov.caixaverso.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.gov.caixaverso.valueobjects.MonetaryValue;
import br.gov.caixaverso.valueobjects.Percentage;

class SimulationTest {

    @Test
    @DisplayName("Deve criar simulacao e totalizar juros e saldo final")
    void shouldCreateSimulationAndCalculateTotals() {

        CalculationMemory month1 = new CalculationMemory(
                1,
                MonetaryValue.from("1000.00"),
                MonetaryValue.from("10.00"));

        CalculationMemory month2 = new CalculationMemory(
                2,
                MonetaryValue.from("1010.00"),
                MonetaryValue.from("10.10"));

        Simulation simulation = new Simulation(
                MonetaryValue.from("1000.00"),
                Percentage.from("1"),
                2,
                List.of(month2, month1));

        assertEquals(MonetaryValue.from("1000.00").getValue(), simulation.getInitialAmount().getValue());
        assertEquals(Percentage.from("1").getValue(), simulation.getMonthlyInterestRate().getValue());
        assertEquals(2, simulation.getTermMonths());
        assertEquals(MonetaryValue.from("20.10").getValue(), simulation.getTotalInterestAmount().getValue());
        assertEquals(MonetaryValue.from("1020.10").getValue(), simulation.getTotalFinalAmount().getValue());
        assertEquals(2, simulation.getCalculationMemories().size());
    }

    @Test
    @DisplayName("Deve associar itens ao pai na criacao")
    void shouldAttachMemoriesToParentOnCreation() {
        CalculationMemory memory = new CalculationMemory(
                1,
                MonetaryValue.from("1000.00"),
                MonetaryValue.from("10.00"));

        Simulation simulation = new Simulation(
                MonetaryValue.from("1000.00"),
                Percentage.from("1"),
                1,
                List.of(memory));

        assertSame(simulation, memory.getSimulation());
    }

    @Test
    @DisplayName("Deve lancar excecao para lista nula")
    void shouldThrowWhenMemoriesIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> new Simulation(
                        MonetaryValue.from("1000.00"),
                        Percentage.from("1"),
                        1,
                        null));

        assertEquals("Lista de memoria de calculo nao pode ser nula", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao para lista vazia")
    void shouldThrowWhenMemoriesIsEmpty() {
        List<CalculationMemory> emptyMemories = List.of();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Simulation(
                        MonetaryValue.from("1000.00"),
                        Percentage.from("1"),
                        1,
                        emptyMemories));

        assertEquals("Lista de memoria de calculo nao pode ser vazia", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao para item nulo na lista")
    void shouldThrowWhenMemoryItemIsNull() {
        List<CalculationMemory> memories = new ArrayList<>();
        memories.add(new CalculationMemory(
                1,
                MonetaryValue.from("1000.00"),
                MonetaryValue.from("10.00")));
        memories.add(null);

        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> new Simulation(
                        MonetaryValue.from("1000.00"),
                        Percentage.from("1"),
                        2,
                        memories));

        assertEquals("Item da memoria de calculo nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve expor lista de memoria apenas para leitura")
    void shouldExposeReadOnlyMemoriesList() {
        CalculationMemory monthOne = new CalculationMemory(
                1,
                MonetaryValue.from("1000.00"),
                MonetaryValue.from("10.00"));

        CalculationMemory monthTwo = new CalculationMemory(
                2,
                MonetaryValue.from("1010.00"),
                MonetaryValue.from("10.10"));

        Simulation simulation = new Simulation(
                MonetaryValue.from("1000.00"),
                Percentage.from("1"),
                1,
                List.of(monthOne));

        assertThrows(UnsupportedOperationException.class,
                () -> simulation.getCalculationMemories().add(monthTwo));
    }

        @Test
        @DisplayName("Deve lancar excecao para prazo em meses menor ou igual a zero")
        void shouldThrowWhenTermMonthsIsInvalid() {
                CalculationMemory memory = new CalculationMemory(
                                1,
                                MonetaryValue.from("1000.00"),
                                MonetaryValue.from("10.00"));

                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> new Simulation(
                                                MonetaryValue.from("1000.00"),
                                                Percentage.from("1"),
                                                0,
                                                List.of(memory)));

                assertEquals("Prazo em meses deve ser maior que 0", ex.getMessage());
        }
}
