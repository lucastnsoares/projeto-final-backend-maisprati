package br.com.maisprati.projeto.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "collection_point")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CollectionPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

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

    @Column(nullable = false, length = 50)
    private String state;

    @Column(nullable = false, length = 50)
    private String country;

    @Column(name = "zip_code", nullable = false, length = 8)
    private String zipCode;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "operating_hours", nullable = false)
    private Set<OperatingHour> operatingHours;

    @Column(name = "cloth_types", nullable = false)
    private Set<ClothType> clothTypes;

    @Column(name = "point_picture_url")
    private String pointPictureUrl;

    @Column(name = "is_active")
    private boolean isActive = false;

    @Column(name = "is_pending",  nullable = false)
    private boolean isPending = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
