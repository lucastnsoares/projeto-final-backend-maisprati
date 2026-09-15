package br.com.maisprati.projeto.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.maisprati.projeto.dto.projection.CollectionPointDistanceProjectionDTO;
import br.com.maisprati.projeto.model.entity.CollectionPoint;
import br.com.maisprati.projeto.model.enums.CollectionPointStatus;

@Repository
public interface CollectionPointRepository extends JpaRepository<CollectionPoint, Long> {

    // Busca pontos de coleta baseados no status
    List<CollectionPoint> findByStatus(CollectionPointStatus status);

    // Busca pontos de coleta ativos próximos a uma localização específica dentro de um raio definido (Fórmula de Haversine)
    @Query(value = """
        SELECT cp.id AS id,
               cp.name AS name,
               CONCAT_WS(', ', cp.street, cp.number, cp.neighborhood, cp.city, cp.state) AS address,
               cp.latitude AS latitude,
               cp.longitude AS longitude,
               (6371 * acos(
                   LEAST(1.0, GREATEST(-1.0,
                       cos(radians(:userLat)) * cos(radians(cp.latitude::float8)) *
                       cos(radians(cp.longitude::float8) - radians(:userLng)) +
                       sin(radians(:userLat)) * sin(radians(cp.latitude::float8))
                   ))
               )) AS distanceKm
        FROM collection_points cp
        WHERE cp.status = 'ACTIVE'
          AND (6371 * acos(
                   LEAST(1.0, GREATEST(-1.0,
                       cos(radians(:userLat)) * cos(radians(cp.latitude::float8)) *
                       cos(radians(cp.longitude::float8) - radians(:userLng)) +
                       sin(radians(:userLat)) * sin(radians(cp.latitude::float8))
                   ))
               )) <= :radiusKm
        ORDER BY distanceKm ASC
        """,
        countQuery = """
        SELECT count(*)
        FROM collection_points cp
        WHERE cp.status = 'ACTIVE'
          AND (6371 * acos(
                   LEAST(1.0, GREATEST(-1.0,
                       cos(radians(:userLat)) * cos(radians(cp.latitude::float8)) *
                       cos(radians(cp.longitude::float8) - radians(:userLng)) +
                       sin(radians(:userLat)) * sin(radians(cp.latitude::float8))
                   ))
               )) <= :radiusKm
        """,
        nativeQuery = true)
    Page<CollectionPointDistanceProjectionDTO> findNearby(
            @Param("userLat") BigDecimal userLat,
            @Param("userLng") BigDecimal userLng,
            @Param("radiusKm") BigDecimal radiusKm,
            Pageable pageable
    );
}