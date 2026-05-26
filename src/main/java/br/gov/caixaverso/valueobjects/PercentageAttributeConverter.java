package br.gov.caixaverso.valueobjects;

import java.math.BigDecimal;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PercentageAttributeConverter implements AttributeConverter<Percentage, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(Percentage attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Percentage convertToEntityAttribute(BigDecimal dbData) {
        return dbData == null ? null : Percentage.from(dbData);
    }
}
