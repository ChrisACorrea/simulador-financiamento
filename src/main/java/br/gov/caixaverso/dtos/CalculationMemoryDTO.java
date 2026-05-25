package br.gov.caixaverso.dtos;

import java.util.List;

import br.gov.caixaverso.entities.CalculationMemory;

public record CalculationMemoryDTO(
        Integer mes,
        String saldoInicial,
        String juro,
        String saldoFinal) {

    public static CalculationMemoryDTO fromEntity(CalculationMemory calculationMemory) {
        return new CalculationMemoryDTO(
                calculationMemory.getMonth(),
                calculationMemory.getInitialBalance().toString(),
                calculationMemory.getInterestAmount().toString(),
                calculationMemory.getFinalBalance().toString());
    }

    public static List<CalculationMemoryDTO> fromEntityList(List<CalculationMemory> calculationMemories) {
        return calculationMemories.stream()
                .map(CalculationMemoryDTO::fromEntity)
                .toList();
    }
}
