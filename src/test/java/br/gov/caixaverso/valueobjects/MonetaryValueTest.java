package br.gov.caixaverso.valueobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.gov.caixaverso.exceptions.MonetaryValueException;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MonetaryValueTest {

    @Test
    @DisplayName("Deve criar valor monetario a partir de BigDecimal")
    void shouldCreateFromBigDecimal() {
        MonetaryValue monetaryValue = new MonetaryValue(new BigDecimal("100.50"));

        assertEquals(new BigDecimal("100.50"), monetaryValue.getValue());
    }

    @Test
    @DisplayName("Deve criar valor monetario a partir de String")
    void shouldCreateFromStringConstructor() {
        MonetaryValue monetaryValue = new MonetaryValue(" 100.50 ");

        assertEquals(new BigDecimal("100.50"), monetaryValue.getValue());
    }

    @Test
    @DisplayName("Deve criar valor monetario via metodo de fabrica com String")
    void shouldCreateFromFactoryMethodWithString() {
        MonetaryValue monetaryValue = MonetaryValue.from("2500");

        assertEquals(new BigDecimal("2500"), monetaryValue.getValue());
    }

    @Test
    @DisplayName("Deve somar dois valores monetarios")
    void shouldAddTwoMonetaryValues() {
        MonetaryValue a = MonetaryValue.from("10");
        MonetaryValue b = MonetaryValue.from("2.5");

        MonetaryValue result = a.add(b);

        assertEquals(new BigDecimal("12.5"), result.getValue());
    }

    @Test
    @DisplayName("Deve subtrair dois valores monetarios")
    void shouldSubtractTwoMonetaryValues() {
        MonetaryValue a = MonetaryValue.from("10");
        MonetaryValue b = MonetaryValue.from("2.5");

        MonetaryValue result = a.subtract(b);

        assertEquals(new BigDecimal("7.5"), result.getValue());
    }

    @Test
    @DisplayName("Deve somar valor base com varargs")
    void shouldAddWithVarargs() {
        MonetaryValue result = MonetaryValue.add(
                MonetaryValue.from("10"),
                MonetaryValue.from("2"),
                MonetaryValue.from("3.5"));

        assertEquals(new BigDecimal("15.5"), result.getValue());
    }

    @Test
    @DisplayName("Deve somar valor base com colecao")
    void shouldAddWithCollection() {
        MonetaryValue result = MonetaryValue.add(
                MonetaryValue.from("10"),
                List.of(MonetaryValue.from("1"), MonetaryValue.from("2")));

        assertEquals(new BigDecimal("13"), result.getValue());
    }

    @Test
    @DisplayName("Deve aplicar percentual sobre o valor")
    void shouldAddPercentage() {
        MonetaryValue base = MonetaryValue.from("100");
        Percentage percentage = Percentage.from("10");

        MonetaryValue result = base.addPercentage(percentage);

        assertEquals(new BigDecimal("110.0"), result.getValue());
    }

    @Test
    @DisplayName("Deve calcular potencia do valor")
    void shouldPowValue() {
        MonetaryValue monetaryValue = MonetaryValue.from("2");

        MonetaryValue result = monetaryValue.pow(3);

        assertEquals(new BigDecimal("8"), result.getValue());
    }

    @Test
    @DisplayName("Deve formatar valor monetario em pt-BR")
    void shouldFormatMonetaryValueUsingPtBr() {
        MonetaryValue monetaryValue = MonetaryValue.from("1234.56");

        assertEquals("R$ 1.234,56", monetaryValue.toString());
    }

    @Test
    @DisplayName("Deve formatar valor monetario em string numerica")
    void shouldFormatMonetaryValueAsNumericString() {
        MonetaryValue monetaryValue = MonetaryValue.from("1234.56");

        assertEquals("1234.56", monetaryValue.toNumericString());
        assertEquals("1234.5600", monetaryValue.toNumericString(4));
    }

    @ParameterizedTest(name = "deve rejeitar entrada textual invalida: {0}")
    @DisplayName("Deve lançar excecao para string invalida")
    @ValueSource(strings = { "abc", "10,5", "1 0" })
    void shouldThrowWhenStringValueIsInvalid(String input) {
        MonetaryValueException ex = assertThrows(MonetaryValueException.class, () -> new MonetaryValue(input));

        assertEquals("Valor monetario invalido: " + input, ex.getMessage());
    }

    @ParameterizedTest(name = "deve rejeitar entrada nula ou vazia")
    @DisplayName("Deve lançar excecao para string nula ou vazia")
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   " })
    void shouldThrowWhenStringValueIsNullOrBlank(String input) {
        MonetaryValueException ex = assertThrows(MonetaryValueException.class, () -> new MonetaryValue(input));

        assertEquals("Valor monetario nao pode ser nulo ou vazio", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar excecao para valor nulo em BigDecimal")
    void shouldThrowWhenBigDecimalValueIsNull() {
        MonetaryValueException ex = assertThrows(MonetaryValueException.class, () -> new MonetaryValue((BigDecimal) null));

        assertEquals("Valor monetario nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar excecao para valor negativo em BigDecimal")
    void shouldThrowWhenBigDecimalValueIsNegative() {
        MonetaryValueException ex = assertThrows(MonetaryValueException.class, () -> new MonetaryValue(new BigDecimal("-0.01")));

        assertEquals("Valor monetario deve ser maior ou igual a 0", ex.getMessage());
    }

    @ParameterizedTest(name = "deve rejeitar expoente {0}")
    @DisplayName("Deve lançar excecao para expoente negativo")
    @CsvSource({ "-1", "-5" })
    void shouldThrowWhenExponentIsNegative(int exponent) {
        MonetaryValueException ex = assertThrows(MonetaryValueException.class, () -> MonetaryValue.from("2").pow(exponent));

        assertEquals("Expoente deve ser maior ou igual a 0", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar excecao ao somar com outro valor nulo")
    void shouldThrowWhenAddingNullMonetaryValue() {
        MonetaryValueException ex = assertThrows(MonetaryValueException.class,
                () -> MonetaryValue.from("10").add(null));

        assertEquals("Outro valor monetario nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar excecao ao subtrair com outro valor nulo")
    void shouldThrowWhenSubtractingNullMonetaryValue() {
        MonetaryValueException ex = assertThrows(MonetaryValueException.class,
                () -> MonetaryValue.from("10").subtract(null));

        assertEquals("Outro valor monetario nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar excecao ao aplicar percentual nulo")
    void shouldThrowWhenAddingNullPercentage() {
        MonetaryValueException ex = assertThrows(MonetaryValueException.class,
                () -> MonetaryValue.from("10").addPercentage(null));

        assertEquals("Percentual nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar excecao ao somar colecao nula")
    void shouldThrowWhenAddingNullCollection() {
        MonetaryValueException ex = assertThrows(MonetaryValueException.class,
                () -> MonetaryValue.add(MonetaryValue.from("10"), (List<MonetaryValue>) null));

        assertEquals("Colecao de valores monetarios nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar excecao para escala negativa na formatacao numerica")
    void shouldThrowWhenNumericFormatScaleIsNegative() {
        MonetaryValueException ex = assertThrows(MonetaryValueException.class,
                () -> MonetaryValue.from("10").toNumericString(-1));

        assertEquals("Escala deve ser maior ou igual a 0", ex.getMessage());
    }
}
