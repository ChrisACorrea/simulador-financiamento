package br.gov.caixaverso.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.gov.caixaverso.dtos.SimulationInputDTO;
import br.gov.caixaverso.dtos.SimulationRead;
import br.gov.caixaverso.entities.CalculationMemory;
import br.gov.caixaverso.entities.Simulation;
import br.gov.caixaverso.exceptions.DomainValidationException;
import br.gov.caixaverso.exceptions.ResourceNotFoundException;
import br.gov.caixaverso.repositories.abstractions.ISimulationRepository;
import br.gov.caixaverso.valueobjects.MonetaryValue;
import br.gov.caixaverso.valueobjects.Percentage;

class SimulationServiceTest {

    @Test
    @DisplayName("Deve simular juros compostos mes a mes")
    void shouldSimulateCompoundInterestMonthByMonth() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        doNothing().when(repository).persist(any(Simulation.class));

        SimulationService service = new SimulationService(repository);
        SimulationInputDTO input = new SimulationInputDTO(
                MonetaryValue.from("1000.00"),
                Percentage.from("1"),
                3);

        SimulationRead result = service.simulate(input);

        assertEquals(null, result.id());
        assertEquals("1000.00", result.valorInicial());
        assertEquals("1", result.taxaJurosMensal());
        assertEquals(3, result.prazoMeses());
        assertEquals("1030.30", result.valorTotalFinal());
        assertEquals("30.30", result.valorTotalJuros());
        assertEquals(3, result.calculos().size());

        assertEquals(1, result.calculos().get(0).mes());
        assertEquals("1000.00", result.calculos().get(0).saldoInicial());
        assertEquals("10.00", result.calculos().get(0).juro());
        assertEquals("1010.00", result.calculos().get(0).saldoFinal());

        assertEquals(2, result.calculos().get(1).mes());
        assertEquals("1010.00", result.calculos().get(1).saldoInicial());
        assertEquals("10.10", result.calculos().get(1).juro());
        assertEquals("1020.10", result.calculos().get(1).saldoFinal());

        assertEquals(3, result.calculos().get(2).mes());
        assertEquals("1020.10", result.calculos().get(2).saldoInicial());
        assertEquals("10.20", result.calculos().get(2).juro());
        assertEquals("1030.30", result.calculos().get(2).saldoFinal());

        verify(repository).persist(any(Simulation.class));
    }

    @Test
    @DisplayName("Deve lancar excecao quando valor inicial for nulo")
    void shouldThrowWhenInitialAmountIsNull() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        SimulationService service = new SimulationService(repository);

        SimulationInputDTO input = new SimulationInputDTO(null, Percentage.from("1"), 12);

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> service.simulate(input));

        assertEquals("Valor inicial nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando taxa de juros mensal for nula")
    void shouldThrowWhenMonthlyInterestRateIsNull() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        SimulationService service = new SimulationService(repository);

        SimulationInputDTO input = new SimulationInputDTO(MonetaryValue.from("1000"), null, 12);

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> service.simulate(input));

        assertEquals("Taxa de juros mensal nao pode ser nula", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando prazo em meses for nulo")
    void shouldThrowWhenTermMonthsIsNull() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        SimulationService service = new SimulationService(repository);

        SimulationInputDTO input = new SimulationInputDTO(MonetaryValue.from("1000"), Percentage.from("1"), null);

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> service.simulate(input));

        assertEquals("Prazo em meses nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando prazo em meses for menor ou igual a zero")
    void shouldThrowWhenTermMonthsIsZeroOrNegative() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        SimulationService service = new SimulationService(repository);

        SimulationInputDTO input = new SimulationInputDTO(MonetaryValue.from("1000"), Percentage.from("1"), 0);

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> service.simulate(input));

        assertEquals("Prazo em meses deve ser maior que 0", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando input for nulo")
    void shouldThrowWhenInputIsNull() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        SimulationService service = new SimulationService(repository);

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> service.simulate(null));

        assertEquals("Dados da simulacao nao podem ser nulos", ex.getMessage());
    }

    @Test
    @DisplayName("Deve listar todas as simulacoes")
    void shouldListAllSimulations() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        SimulationService service = new SimulationService(repository);

        Simulation simulation1 = createSimulation("1000.00", "1", 2);
        Simulation simulation2 = createSimulation("2000.00", "2", 2);
        when(repository.listAll()).thenReturn(List.of(simulation1, simulation2));

        List<SimulationRead> result = service.listAll();

        assertEquals(2, result.size());
        assertEquals("1000.00", result.get(0).valorInicial());
        assertEquals("1", result.get(0).taxaJurosMensal());
        assertEquals(2, result.get(0).prazoMeses());
        assertEquals("1020.10", result.get(0).valorTotalFinal());
        assertEquals("20.10", result.get(0).valorTotalJuros());
        assertEquals("2000.00", result.get(1).valorInicial());
        assertEquals("2", result.get(1).taxaJurosMensal());
        assertEquals(2, result.get(1).prazoMeses());
        assertEquals("2080.80", result.get(1).valorTotalFinal());
        assertEquals("80.80", result.get(1).valorTotalJuros());
        verify(repository).listAll();
    }

    @Test
    @DisplayName("Deve buscar simulacao por id")
    void shouldGetSimulationById() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        SimulationService service = new SimulationService(repository);

        Simulation simulation = createSimulation("1000.00", "1", 2);
        when(repository.findByIdOptional(1L)).thenReturn(Optional.of(simulation));

        SimulationRead result = service.getById(1L);

        assertEquals("1000.00", result.valorInicial());
        assertEquals("1", result.taxaJurosMensal());
        assertEquals(2, result.prazoMeses());
        assertEquals("1020.10", result.valorTotalFinal());
        assertEquals("20.10", result.valorTotalJuros());
        assertEquals(2, result.calculos().size());
        verify(repository).findByIdOptional(1L);
    }

    @Test
    @DisplayName("Deve lancar excecao quando id da busca for nulo")
    void shouldThrowWhenGetByIdIsNull() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        SimulationService service = new SimulationService(repository);

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> service.getById(null));

        assertEquals("Id da simulacao nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando simulacao nao for encontrada por id")
    void shouldThrowWhenSimulationIsNotFoundById() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        SimulationService service = new SimulationService(repository);
        when(repository.findByIdOptional(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));

        assertEquals("Simulacao nao encontrada para o id: 99", ex.getMessage());
    }

    @Test
    @DisplayName("Deve apagar simulacao por id")
    void shouldDeleteSimulationById() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        SimulationService service = new SimulationService(repository);
        when(repository.deleteById(10L)).thenReturn(true);

        service.deleteById(10L);

        verify(repository).deleteById(10L);
    }

    @Test
    @DisplayName("Deve lancar excecao quando id da exclusao for nulo")
    void shouldThrowWhenDeleteByIdIsNull() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        SimulationService service = new SimulationService(repository);

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> service.deleteById(null));

        assertEquals("Id da simulacao nao pode ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando simulacao nao for encontrada para exclusao")
    void shouldThrowWhenDeleteByIdDoesNotFindSimulation() {
        ISimulationRepository repository = org.mockito.Mockito.mock(ISimulationRepository.class);
        SimulationService service = new SimulationService(repository);
        doReturn(false).when(repository).deleteById(15L);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.deleteById(15L));

        assertEquals("Simulacao nao encontrada para o id: 15", ex.getMessage());
    }

    private static Simulation createSimulation(String initialAmount, String monthlyRate, int months) {
        List<CalculationMemory> calculationMemories = new java.util.ArrayList<>();

        MonetaryValue initialBalance = MonetaryValue.from(initialAmount);
        Percentage rate = Percentage.from(monthlyRate);

        for (int i = 1; i <= months; i++) {
            MonetaryValue finalBalance = initialBalance.addPercentage(rate);
            MonetaryValue interestAmount = finalBalance.subtract(initialBalance);
            calculationMemories.add(new CalculationMemory(i, initialBalance, interestAmount));
            initialBalance = finalBalance;
        }

        return new Simulation(
            MonetaryValue.from(initialAmount),
            Percentage.from(monthlyRate),
            months,
            calculationMemories);
    }
}
