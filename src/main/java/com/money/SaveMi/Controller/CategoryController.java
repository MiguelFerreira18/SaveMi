package com.money.SaveMi.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.money.SaveMi.DTO.Category.CategoryDtoOut;
import com.money.SaveMi.DTO.Category.SaveCategoryDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Category;
import com.money.SaveMi.Service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Page<CategoryDtoOut>> getAllCategories(@PageableDefault(5) Pageable pageable) {
        Page<CategoryDtoOut> result = categoryService
                .getAllCategories(pageable)
                .map(
                        category -> new CategoryDtoOut(
                                category.getId(),
                                category.getName(),
                                category.getDescription()));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDtoOut> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);

        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        CategoryDtoOut categoryDtoOut = new CategoryDtoOut(
                category.getId(),
                category.getName(),
                category.getDescription());

        return ResponseEntity.ok(categoryDtoOut);
    }

    @PostMapping
    public ResponseEntity<CategoryDtoOut> saveCategory(@RequestBody SaveCategoryDto categoryDto) {

        Category category = categoryService.saveCategory(categoryDto);

        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        CategoryDtoOut categoryDtoOut = new CategoryDtoOut(
                category.getId(),
                category.getName(),
                category.getDescription());

        return ResponseEntity.ok(categoryDtoOut);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCategories(@RequestBody BulkDeleteDto bulkDeleteDto) {
        categoryService.bulkDelete(bulkDeleteDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(202).build();
    }
}
