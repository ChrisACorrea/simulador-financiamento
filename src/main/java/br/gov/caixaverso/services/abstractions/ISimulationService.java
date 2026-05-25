package br.gov.caixaverso.services.abstractions;

import java.util.List;

import br.gov.caixaverso.dtos.SimulationInputDTO;
import br.gov.caixaverso.dtos.SimulationRead;
import br.gov.caixaverso.entities.Simulation;

public interface ISimulationService extends IServiceBase<Simulation, Long> {

	SimulationRead simulate(SimulationInputDTO input);

	List<SimulationRead> listAll();

	SimulationRead getById(Long id);

	void deleteById(Long id);
}
