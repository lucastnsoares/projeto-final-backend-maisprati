package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.enums.CollectionPointStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Data
@Builder
@JsonPropertyOrder({
    "id",
    "name",
    "status",
    "isPending",
    "pointPictureUrl",
    "address",
    "acceptedClothTypes",
    "operatingHours"
})
@Schema(description = "Dados do ponto de coleta cadastrado")
public class CollectionPointResponseDTO {

    @Schema(description = "ID do ponto de coleta", example = "1")
    private Long id;

    @Schema(description = "Nome do ponto de coleta", example = "EcoPonto Central - Tecidos")
    private String name;

    @Schema(description = "Status do ponto de coleta no sistema", example = "ACTIVE")
    private CollectionPointStatus status;

    private AddressResponseDTO address;

    @Schema(description = "URL da imagem de capa", example = "https://example.jpg")
    private String pointPictureUrl;

    @Schema(description = "Tipos de tecidos aceitos")
    private Set<String> acceptedClothTypes;

    @Schema(description = "Horários de funcionamento")
    private Set<OperatingHourResponseDTO> operatingHours;

    @Data
    @Builder
    @JsonPropertyOrder({
        "street",
        "number",
        "complement",
        "neighborhood",
        "city",
        "state",
        "country",
        "zipCode",
        "latitude",
        "longitude"
    })
    public static class AddressResponseDTO {
        private String street;
        private String number;
        private String complement;
        private String neighborhood;
        private String city;
        private String state;
        private String country;
        private String zipCode;
        private BigDecimal latitude;
        private BigDecimal longitude;
    }

    @Data
    @Builder
    public static class OperatingHourResponseDTO {
        private String dayOfWeek;
        private String openingTime;
        private String closingTime;
    }
}