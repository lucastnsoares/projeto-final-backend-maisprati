package br.com.maisprati.projeto.repository;

import br.com.maisprati.projeto.model.entity.ClothType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface ClothTypeRepository extends JpaRepository<ClothType, Long> {
    Page<ClothType> findAll(Pageable pageable);

    boolean existsByName(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
