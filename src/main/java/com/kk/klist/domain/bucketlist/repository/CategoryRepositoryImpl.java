package com.kk.klist.domain.bucketlist.repository;

import com.kk.klist.domain.bucketlist.domain.entity.Category;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Optional<Category> findByCode(String code) {
        return categoryJpaRepository.findByCode(code);
    }

    @Override
    public Category save(Category category) {
        return categoryJpaRepository.save(category);
    }
}
