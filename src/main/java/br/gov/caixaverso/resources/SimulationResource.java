package br.gov.caixaverso.resources;

import java.util.List;

import br.gov.caixaverso.dtos.SimulationInputDTO;
import br.gov.caixaverso.dtos.SimulationRead;
import br.gov.caixaverso.exceptions.DomainValidationException;
import br.gov.caixaverso.resources.abstractions.ISimulationEndpoint;
import br.gov.caixaverso.services.abstractions.ISimulationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class SimulationResource implements ISimulationEndpoint {

	private final ISimulationService simulationService;

	@Inject
	public SimulationResource(ISimulationService simulationService) {
		this.simulationService = simulationService;
	}

	@Transactional
	@Override
	public Response simulate(SimulationInputDTO input) {
		if (input == null) {
			throw new DomainValidationException("Dados da simulacao nao podem ser nulos");
		}

		SimulationRead read = simulationService.simulate(input);
		return Response.status(Response.Status.CREATED).entity(read).build();
	}

	@Override
	public List<SimulationRead> listAll() {
		return simulationService.listAll();
	}

	@Override
	public SimulationRead getById(Long id) {
		return simulationService.getById(id);
	}

	@Transactional
	@Override
	public Response deleteById(Long id) {
		simulationService.deleteById(id);
		return Response.noContent().build();
	}
}
