package br.gov.caixaverso.dtos;

import java.util.List;

import br.gov.caixaverso.entities.CalculationMemory;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "MemoriaCalculo")
public record CalculationMemoryDTO(
        Integer mes,
        String saldoInicial,
        String juro,
        String saldoFinal) {

    public static CalculationMemoryDTO fromEntity(CalculationMemory calculationMemory) {
        return new CalculationMemoryDTO(
                calculationMemory.getMonth(),
                calculationMemory.getInitialBalance().toNumericString(),
                calculationMemory.getInterestAmount().toNumericString(),
                calculationMemory.getFinalBalance().toNumericString());
    }

    public static List<CalculationMemoryDTO> fromEntityList(List<CalculationMemory> calculationMemories) {
        return calculationMemories.stream()
                .map(CalculationMemoryDTO::fromEntity)
                .toList();
    }
}
