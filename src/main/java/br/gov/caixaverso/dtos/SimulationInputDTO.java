package br.gov.caixaverso.dtos;

import br.gov.caixaverso.valueobjects.MonetaryValue;
import br.gov.caixaverso.valueobjects.Percentage;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "EntradaSimulacao")
public record SimulationInputDTO(
        @Schema(description = "Valor principal da simulacao", implementation = String.class, type = SchemaType.STRING)
        MonetaryValue valorInicial,
        @Schema(description = "Taxa de juros mensal em percentual", implementation = String.class, type = SchemaType.STRING)
        Percentage taxaJurosMensal,
        @Schema(description = "Prazo do contrato em meses", minimum = "1")
        Integer prazoMeses) {

}
