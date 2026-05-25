package br.gov.caixaverso.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import br.gov.caixaverso.entities.abstractions.EntityBase;
import br.gov.caixaverso.valueobjects.MonetaryValue;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_SIMULATION")
/**
 * Entidade raiz da simulacao financeira.
 *
 * <p>
 * A simulacao e criada com a lista de memoria de calculo e calcula
 * automaticamente:
 * <ul>
 * <li>valor total de juros (soma dos juros mensais)</li>
 * <li>valor final total (saldo final do maior mes)</li>
 * </ul>
 */
public class Simulation extends EntityBase {

    // region Estado Persistido
    @Column(name = "TOTAL_FINAL_AMOUNT", insertable = true, updatable = false, nullable = false, precision = 14, scale = 2)
    private MonetaryValue totalFinalAmount;

    @Column(name = "TOTAL_INTEREST_AMOUNT", insertable = true, updatable = false, nullable = false, precision = 14, scale = 2)
    private MonetaryValue totalInterestAmount;

    @OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("month ASC")
    private List<CalculationMemory> calculationMemories = new ArrayList<>();
    // endregion

    // region Construtores
    protected Simulation() {
    }

    /**
     * Cria uma simulacao a partir da memoria de calculo completa.
     *
     * @param memories lista de memoria de calculo (nao nula e nao vazia)
     */
    public Simulation(Collection<CalculationMemory> memories) {
        Objects.requireNonNull(memories, "Lista de memoria de calculo nao pode ser nula");
        if (memories.isEmpty()) {
            throw new IllegalArgumentException("Lista de memoria de calculo nao pode ser vazia");
        }

        List<CalculationMemory> localMemories = new ArrayList<>(memories);
        localMemories.forEach(memory -> {
            Objects.requireNonNull(memory, "Item da memoria de calculo nao pode ser nulo");
            memory.attachTo(this);
        });

        this.calculationMemories.addAll(localMemories);
        this.totalInterestAmount = calculateTotalInterestAmount(localMemories);
        this.totalFinalAmount = calculateTotalFinalAmount(localMemories);
    }
    // endregion

    // region Acessores
    public MonetaryValue getTotalFinalAmount() {
        return totalFinalAmount;
    }

    public MonetaryValue getTotalInterestAmount() {
        return totalInterestAmount;
    }

    public List<CalculationMemory> getCalculationMemories() {
        return Collections.unmodifiableList(calculationMemories);
    }
    // endregion

    // region Regras de Totalizacao
    private MonetaryValue calculateTotalInterestAmount(Collection<CalculationMemory> memories) {
        return memories.stream()
                .map(CalculationMemory::getInterestAmount)
                .reduce(MonetaryValue.ZERO, (accumulator, value) -> accumulator.add(value));
    }

    private MonetaryValue calculateTotalFinalAmount(List<CalculationMemory> memories) {
        return memories.stream()
                .max(Comparator.comparing(CalculationMemory::getMonth))
                .map(CalculationMemory::getFinalBalance)
                .orElseThrow(() -> new IllegalArgumentException("Lista de memoria de calculo nao pode ser vazia"));
    }
    // endregion

}
