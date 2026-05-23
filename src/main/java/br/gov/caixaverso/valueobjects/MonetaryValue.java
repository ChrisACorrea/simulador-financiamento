package br.gov.caixaverso.valueobjects;

import br.gov.caixaverso.exceptions.MonetaryValueException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Locale;

/**
 * Representa um valor monetario imutavel, garantindo validacoes de dominio.
 *
 * <p>
 * Caracteristicas principais:
 * <ul>
 * <li>Nao permite valores nulos ou negativos.</li>
 * <li>Permite criar instancias a partir de {@link BigDecimal} ou {@link String}.</li>
 * <li>Fornece operacoes aritmeticas com preservacao de imutabilidade.</li>
 * </ul>
 */
public class MonetaryValue {

    // region Constantes
    private static final String DEFAULT_LOCALE = "pt-BR";
    // endregion

    // region Estado
    private final BigDecimal value;
    // endregion

    // region Construtores
    /**
     * Cria um valor monetario a partir de {@link BigDecimal}.
     *
     * @param value valor monetario
     * @throws MonetaryValueException se o valor for nulo ou negativo
     */
    public MonetaryValue(BigDecimal value) {
        this.value = validateNonNegative(value, "Valor monetario");
    }

    /**
     * Cria um valor monetario a partir de string numerica.
     *
     * @param value valor monetario em string
     * @throws MonetaryValueException se o texto for nulo, vazio, invalido ou
     *                                representar valor negativo
     */
    public MonetaryValue(String value) {
        this(parseBigDecimal(value, "Valor monetario"));
    }
    // endregion

    // region Metodos de Fabrica
    /**
     * Cria uma instancia a partir de {@link BigDecimal}.
     *
     * @param value valor monetario
     * @return novo {@code MonetaryValue}
     */
    public static MonetaryValue from(BigDecimal value) {
        return new MonetaryValue(value);
    }

    /**
     * Cria uma instancia a partir de string numerica.
     *
     * @param value valor monetario em string
     * @return novo {@code MonetaryValue}
     */
    public static MonetaryValue from(String value) {
        return new MonetaryValue(value);
    }
    // endregion

    // region Acessores
    /**
     * Retorna o valor monetario interno.
     *
     * @return valor monetario
     */
    public BigDecimal getValue() {
        return value;
    }
    // endregion

    // region Operacoes
    /**
     * Soma outro valor monetario ao valor atual.
     *
     * @param other outro valor monetario
     * @return novo {@code MonetaryValue} com o resultado da soma
     */
    public MonetaryValue add(MonetaryValue other) {
        validateNotNull(other, "Outro valor monetario");
        return new MonetaryValue(this.value.add(other.value));
    }

    /**
     * Aplica um percentual sobre o valor atual.
     *
     * @param percentage percentual a aplicar
     * @return novo {@code MonetaryValue} com o valor acrescido do percentual
     */
    public MonetaryValue addPercentage(Percentage percentage) {
        validateNotNull(percentage, "Percentual");
        BigDecimal percentageValue = percentage.getDecimalValue().add(BigDecimal.ONE);
        return new MonetaryValue(this.value.multiply(percentageValue));
    }

    /**
     * Calcula a potencia do valor atual.
     *
     * @param exponent expoente (deve ser maior ou igual a zero)
     * @return novo {@code MonetaryValue} com o resultado da potencia
     */
    public MonetaryValue pow(int exponent) {
        if (exponent < 0) {
            throw new MonetaryValueException("Expoente deve ser maior ou igual a 0");
        }
        return new MonetaryValue(this.value.pow(exponent));
    }

    /**
     * Soma um valor base com uma lista variavel de valores monetarios.
     *
     * @param base valor base
     * @param others demais valores
     * @return novo {@code MonetaryValue} com o total
     */
    public static MonetaryValue add(MonetaryValue base, MonetaryValue... others) {
        validateNotNull(base, "Valor base");
        validateNotNull(others, "Lista de valores monetarios");

        BigDecimal sum = base.value;
        for (MonetaryValue other : others) {
            validateNotNull(other, "Item da lista de valores monetarios");
            sum = sum.add(other.value);
        }

        return new MonetaryValue(sum);
    }

    /**
     * Soma um valor base com uma colecao de valores monetarios.
     *
     * @param base valor base
     * @param others colecao de valores
     * @return novo {@code MonetaryValue} com o total
     */
    public static MonetaryValue add(MonetaryValue base, Collection<MonetaryValue> others) {
        validateNotNull(others, "Colecao de valores monetarios");
        return add(base, others.toArray(new MonetaryValue[0]));
    }
    // endregion

    // region Formatacao
    /**
     * Retorna o valor formatado em moeda com 2 casas decimais.
     *
     * @return valor formatado, ex: R$ 1.234,56
     */
    @Override
    public String toString() {
        return String.format(Locale.forLanguageTag(DEFAULT_LOCALE), "R$ %,.2f", value);
    }
    // endregion

    // region Validacoes e Conversoes Internas
    private static BigDecimal validateNonNegative(BigDecimal value, String label) {
        if (value == null) {
            throw new MonetaryValueException(label + " nao pode ser nulo");
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new MonetaryValueException(label + " deve ser maior ou igual a 0");
        }

        return value;
    }

    private static <T> T validateNotNull(T value, String label) {
        if (value == null) {
            throw new MonetaryValueException(label + " nao pode ser nulo");
        }

        return value;
    }

    private static BigDecimal parseBigDecimal(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new MonetaryValueException(label + " nao pode ser nulo ou vazio");
        }

        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new MonetaryValueException(label + " invalido: " + value, e);
        }
    }
    // endregion
}
