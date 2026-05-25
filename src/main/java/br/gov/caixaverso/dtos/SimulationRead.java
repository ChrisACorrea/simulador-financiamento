package br.gov.caixaverso.dtos;

import java.util.List;

import br.gov.caixaverso.entities.Simulation;

public record SimulationRead(
        Long id,
        String valorTotalFinal,
        String valorTotalJuros,
        List<CalculationMemoryDTO> calculos) {

    public static SimulationRead fromEntity(Simulation simulation) {
        return new SimulationRead(
                simulation.getId(),
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
