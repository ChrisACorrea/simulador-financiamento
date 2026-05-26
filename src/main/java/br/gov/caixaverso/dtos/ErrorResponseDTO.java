package br.gov.caixaverso.dtos;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "RespostaErro")
public record ErrorResponseDTO(String message) {
}
