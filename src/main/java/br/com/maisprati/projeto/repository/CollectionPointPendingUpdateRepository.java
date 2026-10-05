package br.com.maisprati.projeto.repository;

import br.com.maisprati.projeto.model.entity.CollectionPointPendingUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionPointPendingUpdateRepository extends JpaRepository<CollectionPointPendingUpdate, Long> {
    Optional<CollectionPointPendingUpdate> findByCollectionPointId(Long collectionPointId);
    void deleteByCollectionPointId(Long collectionPointId);
}