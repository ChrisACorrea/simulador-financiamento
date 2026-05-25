package br.gov.caixaverso.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.gov.caixaverso.valueobjects.MonetaryValue;

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

        Simulation simulation = new Simulation(List.of(month2, month1));

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

        Simulation simulation = new Simulation(List.of(memory));

        assertSame(simulation, memory.getSimulation());
    }

    @Test
    @DisplayName("Deve lancar excecao para lista nula")
    void shouldThrowWhenMemoriesIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new Simulation(null));

        assertEquals("Lista de memoria de calculo nao pode ser nula", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao para lista vazia")
    void shouldThrowWhenMemoriesIsEmpty() {
        List<CalculationMemory> emptyMemories = List.of();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Simulation(emptyMemories));

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
                () -> new Simulation(memories));

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

        Simulation simulation = new Simulation(List.of(monthOne));

        assertThrows(UnsupportedOperationException.class,
                () -> simulation.getCalculationMemories().add(monthTwo));
    }
}
