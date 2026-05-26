package br.gov.caixaverso.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.gov.caixaverso.exceptions.DomainValidationException;
import br.gov.caixaverso.valueobjects.MonetaryValue;

class CalculationMemoryTest {

    @Test
    @DisplayName("Deve criar item de memoria de calculo com sucesso")
    void shouldCreateCalculationMemory() {
        CalculationMemory memory = new CalculationMemory(
                1,
                MonetaryValue.from("1000.00"),
                MonetaryValue.from("10.00"));

        assertEquals(1, memory.getMonth());
        assertEquals(MonetaryValue.from("1000.00").getValue(), memory.getInitialBalance().getValue());
        assertEquals(MonetaryValue.from("10.00").getValue(), memory.getInterestAmount().getValue());
        assertEquals(MonetaryValue.from("1010.00").getValue(), memory.getFinalBalance().getValue());
        assertNull(memory.getSimulation());
    }

    @Test
    @DisplayName("Deve lancar excecao para mes nulo")
    void shouldThrowWhenMonthIsNull() {
        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> new CalculationMemory(
                        null,
                        MonetaryValue.from("1000.00"),
                MonetaryValue.from("10.00")));

        assertEquals("Mes nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao para saldo inicial nulo")
    void shouldThrowWhenInitialBalanceIsNull() {
        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> new CalculationMemory(
                        1,
                        null,
                MonetaryValue.from("10.00")));

        assertEquals("Saldo inicial nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao para juro nulo")
    void shouldThrowWhenInterestAmountIsNull() {
        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> new CalculationMemory(
                        1,
                        MonetaryValue.from("1000.00"),
                null));

        assertEquals("Juro nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve calcular saldo final como soma de saldo inicial e juros")
    void shouldCalculateFinalBalanceFromInitialAndInterest() {
        CalculationMemory memory = new CalculationMemory(
                1,
                MonetaryValue.from("2500.50"),
                MonetaryValue.from("25.05"));

        assertEquals(MonetaryValue.from("2525.55").getValue(), memory.getFinalBalance().getValue());
    }
}
