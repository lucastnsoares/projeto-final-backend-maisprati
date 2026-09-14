package br.com.maisprati.projeto.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Schema(description = "DTO para criação de um novo ponto de coleta")
public class CollectionPointCreateRequestDTO {

    @Schema(description = "Nome do ponto de coleta", example = "EcoPonto Central - Tecidos")
    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @Schema(description = "Descrição sobre quais tipos de doações são aceitas", example = "Aceitamos retalhos de algodão, jeans e tecidos sintéticos para reciclagem.")
    private String description;

    @NotNull(message = "Os dados de endereço são obrigatórios.")
    @Valid
    private AddressRequestDTO address;

    @Schema(description = "Latitude geográfica do local", example = "-23.561414")
    @NotNull(message = "A latitude é obrigatória.")
    private BigDecimal latitude;

    @Schema(description = "Longitude geográfica do local", example = "-46.655881")
    @NotNull(message = "A longitude é obrigatória.")
    private BigDecimal longitude;

    @Schema(description = "URL da imagem de capa do ponto de coleta", example = "https://example.jpg")
    private String imageUrl;

    @Schema(description = "IDs dos tipos de tecido aceitos pelo ponto de coleta", example = "[1, 2]")
    @NotEmpty(message = "Informe ao menos um tipo de tecido aceito.")
    private Set<Long> clothTypeIds;

    @NotEmpty(message = "Informe os horários de funcionamento.")
    @Valid
    private Set<OperatingHourRequestDTO> operatingHours;

    @Data
    @Schema(description = "Endereço físico do ponto de coleta")
    public static class AddressRequestDTO {

        @Schema(description = "Nome da rua/avenida", example = "Avenida Paulista")
        @NotBlank(message = "A rua é obrigatória.")
        private String street;

        @Schema(description = "Número do imóvel", example = "1000")
        @NotBlank(message = "O número é obrigatório.")
        private String number;

        @Schema(description = "Complemento do endereço", example = "Bloco B - Sala 12")
        private String complement;

        @Schema(description = "Bairro", example = "Bela Vista")
        @NotBlank(message = "O bairro é obrigatório.")
        private String neighborhood;

        @Schema(description = "Cidade", example = "São Paulo")
        @NotBlank(message = "A cidade é obrigatória.")
        private String city;

        @Schema(description = "Sigla do estado (UF)", example = "SP")
        @NotBlank(message = "O estado é obrigatório.")
        private String state;

        @Schema(description = "País", example = "Brasil")
        @NotBlank(message = "O país é obrigatório.")
        private String country;

        @Schema(description = "Código de Endereçamento Postal (somente números)", example = "01310100")
        @NotBlank(message = "O CEP é obrigatório.")
        private String zipCode;
    }

    @Data
    @Schema(description = "Horário de funcionamento do ponto de coleta")
    public static class OperatingHourRequestDTO {

        @Schema(description = "Dia da semana em inglês e caixa alta", example = "MONDAY")
        @NotBlank(message = "O dia da semana é obrigatório.")
        private String dayOfWeek;

        @Schema(description = "Horário de abertura no formato HH:mm", example = "08:00")
        @NotBlank(message = "O horário de abertura é obrigatório.")
        private String openTime;

        @Schema(description = "Horário de fechamento no formato HH:mm", example = "18:00")
        @NotBlank(message = "O horário de fechamento é obrigatório.")
        private String closeTime;
    }
}