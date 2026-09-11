package br.com.maisprati.projeto.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "collection_points")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CollectionPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "collection_point_managers",
            joinColumns = @JoinColumn(name = "collection_point_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false)
    )
    @Builder.Default
    private Set<User> managers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "collection_point_operators",
            joinColumns = @JoinColumn(name = "collection_point_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false)
    )
    @Builder.Default
    private Set<User> operators = new HashSet<>();

    @Embedded
    private Address address;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "collection_point_operating_hours",
            joinColumns = @JoinColumn(name = "collection_point_id", nullable = false)
    )
    private Set<OperatingHour> operatingHours = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "collection_point_cloth_types",
            joinColumns = @JoinColumn(name = "collection_point_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "cloth_type_id", nullable = false)
    )
    private Set<ClothType> clothTypes = new HashSet<>();

    @Column(name = "point_picture_url", length = 2048)
    private String pointPictureUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = false;

    @Column(name = "is_pending", nullable = false)
    private boolean isPending = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
