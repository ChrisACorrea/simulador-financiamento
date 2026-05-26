package br.gov.caixaverso.valueobjects;

import br.gov.caixaverso.exceptions.MonetaryValueException;
import br.gov.caixaverso.exceptions.PercentageException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Representa um valor percentual imutável, com suporte a formatação e conversão
 * entre decimal e percentual.
 *
 * <p>
 * Características principais:
 * <ul>
 * <li>O valor interno é armazenado como decimal (ex: 0.10 para 10%), pois esse
 * formato é mais seguro para cálculos financeiros.</li>
 * <li>Por padrão, entradas são interpretadas como percentual (ex: 10 para
 * 10%).</li>
 * <li>Entrada decimal é aceita apenas por métodos explícitos
 * ({@code fromDecimalValue}).</li>
 * <li>Suporta valores acima de 100%.</li>
 * </ul>
 *
 * <p>
 * Exemplos de uso:
 * 
 * <pre>{@code
 * Percentage p = Percentage.from(new BigDecimal("10")); // 10%
 * Percentage pViaCtor = new Percentage("10"); // 10%
 * Percentage p2 = Percentage.fromDecimalValue(new BigDecimal("0.10")); // 10%
 * String s = p.toString(); // "10,00%"
 * }</pre>
 */
public class Percentage {

    // region Constantes
    private static final BigDecimal HUNDRED = new BigDecimal(100);
    private static final int DEFAULT_DECIMAL_PLACES = 2;
    private static final String DEFAULT_LOCALE = "pt-BR";
    // endregion

    // region Estado
    /** Valor decimal interno (ex: 0.10 para 10%) */
    private final BigDecimal decimalValue;
    // endregion

    // region Construtores
    /**
     * Cria uma instância a partir de um valor percentual (ex: 10 para 10%).
     *
     * @implNote Este é o construtor padrão do VO e interpreta o argumento como
     *           percentual, não como decimal.
     * @param percentageValue valor percentual
     * @throws PercentageException se percentageValue for nulo ou negativo
     */
    public Percentage(BigDecimal percentageValue) {
        this(percentageValue, false);
    }

    /**
     * Cria uma instância a partir de uma string em formato percentual (ex: "10"
     * para 10%).
     *
     * @implNote Este construtor usa {@link BigDecimal#BigDecimal(String)} para
     *           preservar precisão.
     * @param percentageValue valor percentual em string
     * @throws PercentageException se percentageValue for nulo, inválido ou
     *                             negativo
     */
    public Percentage(String percentageValue) {
        this(parseBigDecimal(percentageValue, "Valor percentual"), false);
    }

    private Percentage(BigDecimal value, boolean isDecimalInput) {
        if (isDecimalInput) {
            this.decimalValue = validateNonNegative(value, "Valor decimal");
            return;
        }

        BigDecimal validatedPercentage = validateNonNegative(value, "Valor percentual");
        this.decimalValue = toDecimalValue(validatedPercentage);
    }
    // endregion

    // region Metodos de Fabrica
    /**
     * Cria uma instância a partir de uma string representando valor percentual
     * (ex: "10" para 10%).
     *
     * @param percentageValue valor percentual em string
     * @return novo {@code Percentage}
     * @throws PercentageException se o valor for nulo ou inválido
     */
    public static Percentage from(String percentageValue) {
        return new Percentage(percentageValue);
    }

    /**
     * Cria uma instância a partir de um valor percentual (ex: 10 para 10%).
     *
     * @param percentageValue valor percentual (pode ser maior que 100)
     * @return novo {@code Percentage}
     * @throws PercentageException se o valor for nulo ou negativo
     */
    public static Percentage from(BigDecimal percentageValue) {
        return new Percentage(percentageValue);
    }

    /**
     * Cria uma instância a partir de um valor decimal (ex: 0.10 para 10%).
     *
     * @param decimalValue valor decimal
     * @return novo {@code Percentage}
     * @throws PercentageException se o valor for nulo ou negativo
     */
    public static Percentage fromDecimalValue(BigDecimal decimalValue) {
        return new Percentage(decimalValue, true);
    }

    /**
     * Cria uma instância a partir de uma string de valor decimal (ex: "0.10" para
     * 10%).
     *
     * @param decimalValue valor decimal em string
     * @return novo {@code Percentage}
     * @throws PercentageException se o valor for nulo, inválido ou negativo
     */
    public static Percentage fromDecimalValue(String decimalValue) {
        return new Percentage(parseBigDecimal(decimalValue, "Valor decimal"), true);
    }
    // endregion

    // region Acessores

    /**
     * Retorna o valor percentual equivalente ao valor decimal interno.
     *
     * @return valor percentual (ex: 10 para 10%)
     */
    public BigDecimal getValue() {
        return decimalValue.multiply(HUNDRED);
    }

    /**
     * Retorna o valor decimal interno (ex: 0.10 para 10%).
     *
     * @return valor decimal
     */
    public BigDecimal getDecimalValue() {
        return decimalValue;
    }
    // endregion

    // region Operacoes
    /**
     * Soma outro valor monetario ao valor atual.
     *
     * @param other outro valor monetario
     * @return novo {@code Percentage} com o resultado da soma
     */
    public Percentage add(Percentage other) {
        validateNotNull(other, "Outro valor percentual");
        return new Percentage(this.decimalValue.add(other.decimalValue), true);
    }

    /**
     * Calcula a potencia do valor atual.
     *
     * @param exponent expoente (deve ser maior ou igual a zero)
     * @return novo {@code Percentage} com o resultado da potencia
     */
    public Percentage pow(int exponent) {
        if (exponent < 0) {
            throw new PercentageException("Expoente deve ser maior ou igual a 0");
        }
        return new Percentage(this.decimalValue.pow(exponent), true);
    }

    // region Formatacao

    /**
     * Retorna a representação em string do percentual, incluindo o símbolo "%".
     *
     * @return valor formatado, ex: "10,00%"
     */
    @Override
    public String toString() {
        return toString(true);
    }

    /**
     * Retorna a representação em string do percentual, podendo incluir ou não o
     * símbolo "%".
     *
     * @param includeSymbol se deve incluir o símbolo "%"
     * @return valor formatado, ex: "10,00%" ou "10,00"
     */
    public String toString(boolean includeSymbol) {
        return toString(includeSymbol, DEFAULT_DECIMAL_PLACES);
    }

    /**
     * Retorna a representação em string do percentual, com casas decimais definidas
     * e opção de símbolo.
     *
     * @param includeSymbol se deve incluir o símbolo "%"
     * @param decimalPlaces número de casas decimais
     * @return valor formatado, ex: "10,00%"
     */
    public String toString(boolean includeSymbol, int decimalPlaces) {
        int validatedDecimalPlaces = validateNonNegative(decimalPlaces, "Casas decimais");
        BigDecimal scaledValue = getValue().setScale(validatedDecimalPlaces, RoundingMode.HALF_UP);
        String format = "%." + validatedDecimalPlaces + "f";
        String valueStr = String.format(Locale.forLanguageTag(DEFAULT_LOCALE), format, scaledValue);
        return includeSymbol ? valueStr + "%" : valueStr;
    }

    /**
     * Retorna o valor decimal em string, com número de casas decimais definido.
     *
     * @param scale número de casas decimais
     * @return valor decimal formatado, ex: "0,1000"
     */
    public String toDecimalString(int scale) {
        int validatedScale = validateNonNegative(scale, "Escala");
        BigDecimal scaledValue = decimalValue.setScale(validatedScale, RoundingMode.HALF_UP);
        String format = "%." + validatedScale + "f";
        return String.format(Locale.forLanguageTag(DEFAULT_LOCALE), format, scaledValue);
    }

    /**
     * Retorna o valor percentual em formato numerico sem localizacao.
     *
     * @return valor numerico, ex: 1.25
     */
    public String toNumericString() {
        return getValue().stripTrailingZeros().toPlainString();
    }

    /**
     * Retorna o valor percentual em formato numerico sem localizacao com escala definida.
     *
     * @param decimalPlaces numero de casas decimais
     * @return valor numerico com escala definida
     */
    public String toNumericString(int decimalPlaces) {
        int validatedDecimalPlaces = validateNonNegative(decimalPlaces, "Casas decimais");
        return getValue().setScale(validatedDecimalPlaces, RoundingMode.HALF_UP).toPlainString();
    }
    // endregion

    // region Validacoes e Conversoes Internas
    /**
     * Valida se um objeto não é nulo.
     *
     * @param value valor a validar
     * @param label rótulo usado na mensagem de erro
     * @param <T> tipo do valor
     * @return o próprio valor, quando válido
     * @throws PercentageException se o valor for nulo
     */
    private static <T> T validateNotNull(T value, String label) {
        if (value == null) {
            throw new PercentageException(label + " nao pode ser nulo");
        }
        return value;
    }

    /**
     * Valida se um valor monetário/percentual não é nulo e não é negativo.
     *
     * @param value valor a validar
     * @param label rótulo usado na mensagem de erro
     * @return o próprio valor, quando válido
     * @throws PercentageException se o valor for nulo ou negativo
     */
    private static BigDecimal validateNonNegative(BigDecimal value, String label) {
        if (value == null) {
            throw new PercentageException(label + " nao pode ser nulo");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new PercentageException(label + " deve ser maior ou igual a 0");
        }
        return value;
    }

    /**
     * Valida se um valor inteiro não é negativo.
     *
     * @param value valor a validar
     * @param label rótulo usado na mensagem de erro
     * @return o próprio valor, quando válido
     * @throws PercentageException se o valor for negativo
     */
    private static int validateNonNegative(int value, String label) {
        if (value < 0) {
            if ("Casas decimais".equals(label)) {
                throw new PercentageException(label + " devem ser maiores ou iguais a 0");
            }
            throw new PercentageException(label + " deve ser maior ou igual a 0");
        }
        return value;
    }

    /**
     * Converte uma string em {@link BigDecimal} com validação de nulidade e
     * mensagem de erro amigável para domínio.
     *
     * @param value texto com valor numérico
     * @param label rótulo usado na mensagem de erro
     * @return valor numérico convertido
     * @throws PercentageException se o texto for nulo ou inválido
     */
    private static BigDecimal parseBigDecimal(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new PercentageException(label + " nao pode ser nulo ou vazio");
        }

        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new PercentageException(label + " invalido: " + value, e);
        }
    }

    /**
     * Converte um valor percentual para sua representação decimal.
     *
     * <p>
     * Exemplo:
     * <ul>
     * <li>{@code 10} se torna {@code 0.10}
     * <li>{@code 250} se torna {@code 2.50}
     * </ul>
     *
     * @param percentageValue valor percentual
     * @return valor decimal equivalente
     */
    private static BigDecimal toDecimalValue(BigDecimal percentageValue) {
        return percentageValue.divide(HUNDRED);
    }
    // endregion
}
