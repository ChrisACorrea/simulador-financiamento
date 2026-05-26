package br.gov.caixaverso.dtos;

import java.util.List;

import br.gov.caixaverso.entities.Simulation;

public record SimulationRead(
        Long id,
        String valorInicial,
        String taxaJurosMensal,
        Integer prazoMeses,
        String valorTotalFinal,
        String valorTotalJuros,
        List<CalculationMemoryDTO> calculos) {

    public static SimulationRead fromEntity(Simulation simulation) {
        return new SimulationRead(
                simulation.getId(),
                simulation.getInitialAmount().getValue().toPlainString(),
                simulation.getMonthlyInterestRate().getValue().stripTrailingZeros().toPlainString(),
                simulation.getTermMonths(),
                simulation.getTotalFinalAmount().toString(),
                simulation.getTotalInterestAmount().toString(),
                CalculationMemoryDTO.fromEntityList(simulation.getCalculationMemories()));
    }

    public static List<SimulationRead> fromEntityList(List<Simulation> simulations) {
        return simulations.stream()
                .map(SimulationRead::fromEntity)
                .toList();
    }
}
