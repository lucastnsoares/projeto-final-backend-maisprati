package br.com.maisprati.projeto.repository;

import br.com.maisprati.projeto.model.entity.Disposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface DisposalRepository extends JpaRepository<Disposal, Long> {

    // Retorna os descartes de um ponto específico paginados
    Page<Disposal> findByCollectionPointId(Long collectionPointId, Pageable pageable);

    // Calcula a soma total de todos os descartes (poderia adicionar AND d.status = 'CONFIRMED' se desejar somar só os aceitos)
    @Query("SELECT COALESCE(SUM(d.approximateWeightInKg), 0) FROM Disposal d WHERE d.collectionPoint.id = :collectionPointId")
    BigDecimal getTotalWeightByCollectionPointId(@Param("collectionPointId") Long collectionPointId);
}