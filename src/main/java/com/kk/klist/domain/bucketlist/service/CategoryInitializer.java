package com.kk.klist.domain.bucketlist.service;

import com.kk.klist.domain.bucketlist.domain.entity.Category;
import com.kk.klist.domain.bucketlist.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CategoryInitializer implements ApplicationRunner {

    private static final List<CategorySeed> CATEGORY_SEEDS = List.of(
            new CategorySeed("K_POP", "K-pop"),
            new CategorySeed("K_BEAUTY", "K-beauty"),
            new CategorySeed("K_DRAMA", "K-drama"),
            new CategorySeed("K_FOOD", "K-food")
    );

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        CATEGORY_SEEDS.forEach(seed -> categoryRepository.findByCode(seed.code())
                .orElseGet(() -> categoryRepository.save(Category.create(seed.code(), seed.displayName()))));
    }

    private record CategorySeed(String code, String displayName) {
    }
}
