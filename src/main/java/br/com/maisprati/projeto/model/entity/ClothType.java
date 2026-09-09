package br.com.maisprati.projeto.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cloth_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClothType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true,  length = 30)
    private String type;

    @Column(length = 255)
    private String description;


}
