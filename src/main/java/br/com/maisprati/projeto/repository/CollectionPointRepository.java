package br.com.maisprati.projeto.repository;

import br.com.maisprati.projeto.model.entity.CollectionPoint;
import br.com.maisprati.projeto.model.enums.CollectionPointStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionPointRepository extends JpaRepository<CollectionPoint, Long> {

    // Busca pontos de coleta baseados no status
    List<CollectionPoint> findByStatus(CollectionPointStatus status);
}