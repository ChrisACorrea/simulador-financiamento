package br.gov.caixaverso.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.gov.caixaverso.dtos.SimulationInputDTO;
import br.gov.caixaverso.dtos.SimulationRead;
import br.gov.caixaverso.entities.CalculationMemory;
import br.gov.caixaverso.entities.Simulation;
import br.gov.caixaverso.repositories.abstractions.ISimulationRepository;
import br.gov.caixaverso.services.abstractions.ISimulationService;
import br.gov.caixaverso.valueobjects.MonetaryValue;
import br.gov.caixaverso.valueobjects.Percentage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SimulationService implements ISimulationService {

    private final ISimulationRepository simulationRepository;

    @Inject
    public SimulationService(ISimulationRepository simulationRepository) {
        this.simulationRepository = simulationRepository;
    }

    @Override
    public SimulationRead simulate(SimulationInputDTO input) {
        Objects.requireNonNull(input, "Dados da simulacao nao podem ser nulos");

        List<CalculationMemory> calculationMemories = new ArrayList<>();

        MonetaryValue initialBalance = input.valorInicial();

        for (int i = 1; i <= input.prazoMeses(); i++) {
            MonetaryValue interestAmount = calculateMonthlyInterestAmount(initialBalance, input.taxaJurosMensal());
            calculationMemories.add(new CalculationMemory(i, initialBalance, interestAmount));
            initialBalance = initialBalance.add(interestAmount);
        }

        Simulation simulation = new Simulation(calculationMemories);
        simulationRepository.persist(simulation);

        return SimulationRead.fromEntity(simulation);
    }

    @Override
    public List<SimulationRead> listAll() {
        return SimulationRead.fromEntityList(simulationRepository.listAll());
    }

    @Override
    public SimulationRead getById(Long id) {
        Objects.requireNonNull(id, "Id da simulacao nao pode ser nulo");

        Simulation simulation = simulationRepository.findByIdOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Simulacao nao encontrada para o id: " + id));

        return SimulationRead.fromEntity(simulation);
    }

    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Id da simulacao nao pode ser nulo");

        boolean deleted = simulationRepository.deleteById(id);
        if (!deleted) {
            throw new IllegalArgumentException("Simulacao nao encontrada para o id: " + id);
        }
    }

    private MonetaryValue calculateMonthlyInterestAmount(MonetaryValue initialBalance, Percentage monthlyRate) {
        MonetaryValue finalBalance = initialBalance.addPercentage(monthlyRate);
        return finalBalance.subtract(initialBalance);
    }
}
