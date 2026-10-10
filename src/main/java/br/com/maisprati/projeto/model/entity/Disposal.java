package br.com.maisprati.projeto.model.entity;

import br.com.maisprati.projeto.model.enums.DisposalStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "disposals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Disposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Doador

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_point_id", nullable = false)
    private CollectionPoint collectionPoint; // Onde foi entregue

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "disposal_cloth_types",
        joinColumns = @JoinColumn(name = "disposal_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "cloth_type_id", nullable = false)
    )
    @Builder.Default
    private Set<ClothType> clothTypes = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by_id")
    private User confirmedBy;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "approximate_weight_in_kg", precision = 10, scale = 2)
    private BigDecimal approximateWeightInKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private DisposalStatus status = DisposalStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}