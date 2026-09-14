package br.com.maisprati.projeto.repository;

import br.com.maisprati.projeto.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}
