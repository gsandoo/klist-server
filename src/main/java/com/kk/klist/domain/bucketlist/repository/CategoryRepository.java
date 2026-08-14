package com.kk.klist.domain.bucketlist.repository;

import com.kk.klist.domain.bucketlist.domain.entity.Category;
import java.util.Optional;

public interface CategoryRepository {

    Optional<Category> findByCode(String code);

    Category save(Category category);
}
