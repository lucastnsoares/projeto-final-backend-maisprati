package br.com.maisprati.projeto.mapper;

import br.com.maisprati.projeto.dto.request.CollectionPointUpdateDTO;
import br.com.maisprati.projeto.model.entity.Address;
import br.com.maisprati.projeto.model.entity.CollectionPoint;
import br.com.maisprati.projeto.model.entity.OperatingHour;
import br.com.maisprati.projeto.util.StringNormalizer;
import org.mapstruct.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Mapper(componentModel = "spring", uses = { StringNormalizer.class })
public interface CollectionPointMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "name", qualifiedByName = "toUpperCase")
    @Mapping(target = "pointPictureUrl", source = "imageUrl", qualifiedByName = "trimOnly")
    @Mapping(target = "clothTypes", ignore = true)     // Gerenciado manualmente no service via repositório
    @Mapping(target = "operatingHours", ignore = true) // Atualizado manualmente para evitar perda de vínculo JPA
    void updateEntityFromDto(CollectionPointUpdateDTO dto, @MappingTarget CollectionPoint entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "street", qualifiedByName = "toUpperCase")
    @Mapping(target = "number", qualifiedByName = "toUpperCase")
    @Mapping(target = "complement", qualifiedByName = "toUpperCase")
    @Mapping(target = "neighborhood", qualifiedByName = "toUpperCase")
    @Mapping(target = "city", qualifiedByName = "toUpperCase")
    @Mapping(target = "country", qualifiedByName = "toUpperCase")
    @Mapping(target = "zipCode", qualifiedByName = "trimOnly")
    @Mapping(target = "state", expression = "java(dto.state() != null && !dto.state().isBlank() ? br.com.maisprati.projeto.model.enums.State.valueOf(dto.state().trim().toUpperCase()) : entity.getState())")
    void updateAddressFromDto(CollectionPointUpdateDTO.AddressRequestDTO dto, @MappingTarget Address entity);

    @Mapping(target = "dayOfWeek", expression = "java(java.time.DayOfWeek.valueOf(dto.dayOfWeek().trim().toUpperCase()))")
    @Mapping(target = "openingTime", expression = "java(java.time.LocalTime.parse(dto.openTime()))")
    @Mapping(target = "closingTime", expression = "java(java.time.LocalTime.parse(dto.closeTime()))")
    OperatingHour toOperatingHourEntity(CollectionPointUpdateDTO.OperatingHourRequestDTO dto);
}