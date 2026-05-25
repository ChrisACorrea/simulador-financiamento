package br.gov.caixaverso.dtos;

import br.gov.caixaverso.valueobjects.MonetaryValue;
import br.gov.caixaverso.valueobjects.Percentage;

public record SimulationInputDTO(
        MonetaryValue valorInicial,
        Percentage taxaJurosMensal,
        Integer prazoMeses) {

}
