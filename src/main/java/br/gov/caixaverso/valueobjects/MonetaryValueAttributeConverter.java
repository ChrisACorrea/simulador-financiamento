package br.gov.caixaverso.valueobjects;

import java.math.BigDecimal;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MonetaryValueAttributeConverter implements AttributeConverter<MonetaryValue, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(MonetaryValue attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public MonetaryValue convertToEntityAttribute(BigDecimal dbData) {
        return dbData == null ? null : MonetaryValue.from(dbData);
    }
}
