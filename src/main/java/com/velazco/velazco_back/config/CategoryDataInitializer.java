package com.velazco.velazco_back.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.velazco.velazco_back.model.Category;
import com.velazco.velazco_back.repositories.CategoryRepository;

@Component
public class CategoryDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public CategoryDataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<String> defaultCategories = List.of(
            "Bocaditos Dulces", 
            "Bocaditos Salados", 
            "Pasteles", 
            "Postres", 
            "Bebidas",
            "Panadería"
        );

        for (String categoryName : defaultCategories) {
            if (categoryRepository.findByName(categoryName).isEmpty()) {
                Category category = new Category();
                category.setName(categoryName);
                category.setDescription("Categoría para " + categoryName.toLowerCase());
                categoryRepository.save(category);
            }
        }

        System.out.println("✅ Categorías inicializadas exitosamente.");
    }
}
