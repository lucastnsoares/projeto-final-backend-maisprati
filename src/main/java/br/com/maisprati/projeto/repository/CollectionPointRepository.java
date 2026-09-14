package br.com.maisprati.projeto.repository;

import br.com.maisprati.projeto.model.entity.CollectionPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionPointRepository extends JpaRepository<CollectionPoint, Long> {

    // Busca pontos de coleta baseados no status de pendência
    List<CollectionPoint> findByIsPending(boolean isPending);

    // Busca pontos de coleta baseados no status de ativação
    List<CollectionPoint> findByIsActive(boolean isActive);

    // Busca pontos que já foram aprovados (não estão pendentes) e estão ativos
    List<CollectionPoint> findByIsPendingFalseAndIsActiveTrue();
}