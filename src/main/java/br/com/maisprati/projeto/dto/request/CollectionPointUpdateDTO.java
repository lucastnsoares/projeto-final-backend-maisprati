package br.com.maisprati.projeto.dto.request;

import java.math.BigDecimal;
import java.util.Set;

import br.com.maisprati.projeto.model.enums.CollectionPointStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

public record CollectionPointUpdateDTO(
    @Schema(description = "Nome do ponto de coleta", example = "EcoPonto Central - Tecidos")
    String name,

    @Schema(description = "Endereço físico do ponto de coleta")
    @Valid
    AddressRequestDTO address,

    @Schema(description = "Status do ponto de coleta no sistema", example = "ACTIVE")
    CollectionPointStatus status,

    @Schema(description = "URL da imagem de capa do ponto de coleta", example = "https://example.jpg")
    String imageUrl,

    @Schema(description = "IDs dos tipos de tecido aceitos pelo ponto de coleta", example = "[1, 2]")
    Set<Long> clothTypeIds,

    @Valid
    Set<OperatingHourRequestDTO> operatingHour
) {
    @Schema(description = "Endereço físico do ponto de coleta")
    public record AddressRequestDTO(
        @Schema(description = "Nome da rua/avenida", example = "Avenida Paulista")
        String street,

        @Schema(description = "Número do imóvel", example = "1000")
        String number,

        @Schema(description = "Complemento do endereço", example = "Bloco B - Sala 12")
        String complement,

        @Schema(description = "Bairro", example = "Bela Vista")
        String neighborhood,

        @Schema(description = "Cidade", example = "São Paulo")
        String city,

        @Schema(description = "Sigla do estado (UF)", example = "SP")
        String state,

        @Schema(description = "País", example = "Brasil")
        String country,

        @Schema(description = "Código de Endereçamento Postal (somente números)", example = "01310100")
        String zipCode,

        @Schema(description = "Latitude geográfica do local", example = "-23.561414")
        BigDecimal latitude,

        @Schema(description = "Longitude geográfica do local", example = "-46.655881")
        BigDecimal longitude
    ) {}

    @Schema(description = "Horário de funcionamento do ponto de coleta")
    public record OperatingHourRequestDTO(
        @Schema(description = "Dia da semana em inglês e caixa alta", example = "MONDAY")
        String dayOfWeek,

        @Schema(description = "Horário de abertura no formato HH:mm", example = "08:00")
        String openTime,

        @Schema(description = "Horário de fechamento no formato HH:mm", example = "18:00")
        String closeTime
    ) {}
}