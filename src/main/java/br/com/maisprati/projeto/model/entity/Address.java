package br.com.maisprati.projeto.model.entity;

import br.com.maisprati.projeto.model.enums.State;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Column(nullable = false, length = 120)
    private String street;

    @Column(nullable = false, length = 10)
    private String number;

    @Column(length = 30)
    private String complement;

    @Column(nullable = false, length = 50)
    private String neighborhood;

    @Column(nullable = false, length = 50)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 2)
    private State state;

    @Column(nullable = false, length = 50)
    private String country;

    @Column(name = "zip_code", nullable = false, length = 8)
    private String zipCode;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;
}
