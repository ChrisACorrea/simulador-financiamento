package br.gov.caixaverso.resources;

import java.util.List;

import br.gov.caixaverso.dtos.SimulationInputDTO;
import br.gov.caixaverso.dtos.SimulationRead;
import br.gov.caixaverso.resources.abstractions.ISimulationEndpoint;
import br.gov.caixaverso.services.abstractions.ISimulationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
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
			throw new BadRequestException("Dados da simulacao nao podem ser nulos");
		}

		try {
			SimulationRead read = simulationService.simulate(input);
			return Response.status(Response.Status.CREATED).entity(read).build();
		} catch (NullPointerException | IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
	}

	@Override
	public List<SimulationRead> listAll() {
		return simulationService.listAll();
	}

	@Override
	public SimulationRead getById(Long id) {
		try {
			return simulationService.getById(id);
		} catch (IllegalArgumentException e) {
			throw new NotFoundException(e.getMessage());
		}
	}

	@Transactional
	@Override
	public Response deleteById(Long id) {
		try {
			simulationService.deleteById(id);
			return Response.noContent().build();
		} catch (IllegalArgumentException e) {
			throw new NotFoundException(e.getMessage());
		}
	}
}
