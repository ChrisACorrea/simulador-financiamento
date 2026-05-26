package br.gov.caixaverso.config;

import br.gov.caixaverso.dtos.SimulationInputDTO;
import br.gov.caixaverso.repositories.abstractions.ISimulationRepository;
import br.gov.caixaverso.services.abstractions.ISimulationService;
import br.gov.caixaverso.valueobjects.MonetaryValue;
import br.gov.caixaverso.valueobjects.Percentage;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SimulationStartupSeeder {

    private final ISimulationService simulationService;
    private final ISimulationRepository simulationRepository;

    @Inject
    public SimulationStartupSeeder(ISimulationService simulationService, ISimulationRepository simulationRepository) {
        this.simulationService = simulationService;
        this.simulationRepository = simulationRepository;
    }

    @Transactional
    void onStart(@Observes StartupEvent event) {
        if (LaunchMode.current() == LaunchMode.TEST) {
            return;
        }

        if (simulationRepository.count() > 0) {
            return;
        }

        seedSimulations();
    }

    private void seedSimulations() {
        simulationService.simulate(new SimulationInputDTO(MonetaryValue.from("1000.00"), Percentage.from("1.00"), 12));
        simulationService.simulate(new SimulationInputDTO(MonetaryValue.from("2500.00"), Percentage.from("0.75"), 24));
        simulationService.simulate(new SimulationInputDTO(MonetaryValue.from("5000.00"), Percentage.from("1.25"), 18));
        simulationService.simulate(new SimulationInputDTO(MonetaryValue.from("10000.00"), Percentage.from("0.90"), 36));
        simulationService.simulate(new SimulationInputDTO(MonetaryValue.from("15000.00"), Percentage.from("1.10"), 48));
    }
}