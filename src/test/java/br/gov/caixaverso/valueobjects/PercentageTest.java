package br.gov.caixaverso.valueobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.gov.caixaverso.exceptions.PercentageException;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PercentageTest {

    @Test
    @DisplayName("Deve criar a partir de percentual em BigDecimal")
    void shouldCreateFromPercentageValueAndReturnDecimalAndPercentageValues() {
        Percentage percentage = Percentage.from(new BigDecimal("10"));

        assertEquals(new BigDecimal("0.1"), percentage.getDecimalValue());
        assertEquals(new BigDecimal("10.0"), percentage.getValue());
    }

    @Test
    @DisplayName("Deve criar a partir de percentual em String")
    void shouldCreateFromPercentageString() {
        Percentage percentage = Percentage.from("10.5");

        assertEquals(new BigDecimal("0.105"), percentage.getDecimalValue());
        assertEquals(new BigDecimal("10.500"), percentage.getValue());
    }

    @Test
    @DisplayName("Deve criar usando construtor público com BigDecimal")
    void shouldCreateUsingPublicBigDecimalConstructor() {
        Percentage percentage = new Percentage(new BigDecimal("25"));

        assertEquals(new BigDecimal("0.25"), percentage.getDecimalValue());
        assertEquals(new BigDecimal("25.00"), percentage.getValue());
    }

    @Test
    @DisplayName("Deve criar usando construtor público com String")
    void shouldCreateUsingPublicStringConstructor() {
        Percentage percentage = new Percentage("2.5");

        assertEquals(new BigDecimal("0.025"), percentage.getDecimalValue());
        assertEquals(new BigDecimal("2.500"), percentage.getValue());
    }

    @Test
    @DisplayName("Deve criar a partir de decimal em BigDecimal")
    void shouldCreateFromDecimalBigDecimal() {
        Percentage percentage = Percentage.fromDecimalValue(new BigDecimal("0.275"));

        assertEquals(new BigDecimal("0.275"), percentage.getDecimalValue());
        assertEquals(new BigDecimal("27.500"), percentage.getValue());
    }

    @Test
    @DisplayName("Deve criar a partir de decimal em String")
    void shouldCreateFromDecimalStringUsingAliasMethod() {
        Percentage percentage = Percentage.fromDecimalValue("1.25");

        assertEquals(new BigDecimal("1.25"), percentage.getDecimalValue());
        assertEquals(new BigDecimal("125.00"), percentage.getValue());
    }

    @Test
    @DisplayName("Deve suportar valores maiores que cem por cento")
    void shouldSupportValuesGreaterThanOneHundredPercent() {
        Percentage percentage = Percentage.from(new BigDecimal("250"));

        assertEquals(new BigDecimal("2.5"), percentage.getDecimalValue());
        assertEquals("250,00%", percentage.toString());
    }

    @ParameterizedTest(name = "deve formatar percentual com includeSymbol={0}, casas={1}")
    @DisplayName("Deve formatar percentual com parâmetros")
    @CsvSource(value = {
            "true;2;12,35%",
            "false;2;12,35",
            "true;4;12,3450%"
    }, delimiter = ';')
    void shouldFormatToStringWithParameters(boolean includeSymbol, int decimalPlaces, String expectedValue) {
        Percentage percentage = Percentage.fromDecimalValue(new BigDecimal("0.12345"));

        assertEquals(expectedValue, percentage.toString(includeSymbol, decimalPlaces));
    }

    @ParameterizedTest(name = "deve formatar decimal {0} com escala {1}")
    @DisplayName("Deve formatar valor decimal com escala parametrizada")
    @CsvSource(value = {
            "0.1;6;0,100000",
            "0.123456;4;0,1235"
    }, delimiter = ';')
    void shouldFormatDecimalStringWithScale(String decimalValue, int scale, String expectedValue) {
        Percentage percentage = Percentage.fromDecimalValue(new BigDecimal(decimalValue));

        assertEquals(expectedValue, percentage.toDecimalString(scale));
    }

    @Test
    @DisplayName("Deve lançar exceção ao receber percentual nulo em String")
    void shouldThrowWhenFromStringReceivesNull() {
        PercentageException ex = assertThrows(PercentageException.class, () -> Percentage.from((String) null));

        assertEquals("Valor percentual nao pode ser nulo ou vazio", ex.getMessage());
    }

    @ParameterizedTest(name = "deve rejeitar percentual nulo ou vazio: {0}")
    @DisplayName("Deve lançar exceção para percentual nulo ou vazio em String")
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   " })
    void shouldThrowWhenFromStringReceivesBlank(String input) {
        PercentageException ex = assertThrows(PercentageException.class, () -> Percentage.from(input));

        assertEquals("Valor percentual nao pode ser nulo ou vazio", ex.getMessage());
    }

    @ParameterizedTest(name = "deve rejeitar percentual em String: {0}")
    @DisplayName("Deve lançar exceção para percentual inválido em String")
    @CsvSource(value = {
            "invalid;Valor percentual invalido: invalid",
            "-1;Valor percentual deve ser maior ou igual a 0"
    }, delimiter = ';')
    void shouldThrowWhenFromStringReceivesInvalidOrNegativeValue(String input, String expectedMessage) {
        PercentageException ex = assertThrows(PercentageException.class, () -> Percentage.from(input));

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao receber percentual nulo em BigDecimal")
    void shouldThrowWhenFromPercentageBigDecimalReceivesNull() {
        PercentageException ex = assertThrows(PercentageException.class, () -> Percentage.from((BigDecimal) null));

        assertEquals("Valor percentual nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao receber percentual negativo em BigDecimal")
    void shouldThrowWhenFromPercentageBigDecimalReceivesNegativeValue() {
        PercentageException ex = assertThrows(PercentageException.class, () -> Percentage.from(new BigDecimal("-1")));

        assertEquals("Valor percentual deve ser maior ou igual a 0", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao receber decimal nulo em BigDecimal")
    void shouldThrowWhenFromDecimalBigDecimalReceivesNull() {
        PercentageException ex = assertThrows(PercentageException.class,
                () -> Percentage.fromDecimalValue((BigDecimal) null));

        assertEquals("Valor decimal nao pode ser nulo", ex.getMessage());
    }

    @ParameterizedTest(name = "deve rejeitar decimal nulo ou vazio: {0}")
    @DisplayName("Deve lançar exceção para decimal nulo ou vazio em String")
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   " })
    void shouldThrowWhenFromDecimalStringReceivesBlank(String input) {
        PercentageException ex = assertThrows(PercentageException.class,
                () -> Percentage.fromDecimalValue(input));

        assertEquals("Valor decimal nao pode ser nulo ou vazio", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao receber decimal negativo em BigDecimal")
    void shouldThrowWhenFromDecimalBigDecimalReceivesNegativeValue() {
        PercentageException ex = assertThrows(PercentageException.class,
                () -> Percentage.fromDecimalValue(new BigDecimal("-0.01")));

        assertEquals("Valor decimal deve ser maior ou igual a 0", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para escala negativa")
    void shouldThrowWhenScaleIsNegative() {
        Percentage percentage = Percentage.from(new BigDecimal("10"));

        PercentageException ex = assertThrows(PercentageException.class, () -> percentage.toDecimalString(-1));

        assertEquals("Escala deve ser maior ou igual a 0", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para casas decimais negativas")
    void shouldThrowWhenDecimalPlacesIsNegative() {
        Percentage percentage = Percentage.from(new BigDecimal("10"));

        PercentageException ex = assertThrows(PercentageException.class, () -> percentage.toString(true, -1));

        assertEquals("Casas decimais devem ser maiores ou iguais a 0", ex.getMessage());
    }
}
