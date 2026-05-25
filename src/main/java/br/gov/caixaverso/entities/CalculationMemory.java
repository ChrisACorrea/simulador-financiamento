package br.gov.caixaverso.entities;

import java.util.Objects;

import br.gov.caixaverso.entities.abstractions.EntityBase;
import br.gov.caixaverso.valueobjects.MonetaryValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "TB_CALCULATION_MEMORY", uniqueConstraints = {
        @UniqueConstraint(name = "UK_CALC_MEMORY_SIM_MONTH", columnNames = { "SIMULATION_ID", "MONTH" })
})
/**
 * Item mensal da memoria de calculo de uma simulacao.
 *
 * <p>
 * Cada item representa um mes unico dentro da simulacao:
 * mes, saldo inicial, valor de juros e saldo final.
 */
public class CalculationMemory extends EntityBase {

    // region Estado Persistido
    @Column(name = "MONTH", insertable = true, updatable = false, nullable = false)
    private Integer month;

    @Column(name = "INITIAL_BALANCE", insertable = true, updatable = false, nullable = false, precision = 14, scale = 2)
    private MonetaryValue initialBalance;

    @Column(name = "INTEREST_AMOUNT", insertable = true, updatable = false, nullable = false, precision = 14, scale = 2)
    private MonetaryValue interestAmount;

    @Column(name = "FINAL_BALANCE", insertable = true, updatable = false, nullable = false, precision = 14, scale = 2)
    private MonetaryValue finalBalance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SIMULATION_ID", nullable = false, updatable = false)
    private Simulation simulation;
    // endregion

    // region Construtores
    protected CalculationMemory() {
    }

    /**
     * Cria um item de memoria de calculo.
     *
     * @param month          mes de referencia do item
     * @param initialBalance saldo no inicio do mes
     * @param interestAmount valor de juros aplicado no mes
     * @param finalBalance   saldo ao fim do mes
     */
    public CalculationMemory(Integer month, MonetaryValue initialBalance, MonetaryValue interestAmount,
            MonetaryValue finalBalance) {
        this.month = Objects.requireNonNull(month, "Mes nao pode ser nulo");
        this.initialBalance = Objects.requireNonNull(initialBalance, "Saldo inicial nao pode ser nulo");
        this.interestAmount = Objects.requireNonNull(interestAmount, "Juro nao pode ser nulo");
        this.finalBalance = Objects.requireNonNull(finalBalance, "Saldo final nao pode ser nulo");
    }
    // endregion

    // region Acessores
    public Integer getMonth() {
        return month;
    }

    public MonetaryValue getInitialBalance() {
        return initialBalance;
    }

    public MonetaryValue getInterestAmount() {
        return interestAmount;
    }

    public MonetaryValue getFinalBalance() {
        return finalBalance;
    }

    public Simulation getSimulation() {
        return simulation;
    }
    // endregion

    // region Associacao Interna
    void attachTo(Simulation simulation) {
        this.simulation = simulation;
    }
    // endregion
}
