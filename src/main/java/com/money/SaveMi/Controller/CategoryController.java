package com.money.SaveMi.Controller;

import com.money.SaveMi.DTO.Category.CategoryDtoOut;
import com.money.SaveMi.DTO.Category.SaveCategoryDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Category;
import com.money.SaveMi.Service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/all")
    public ResponseEntity<Iterable<CategoryDtoOut>> getAllCategories() {
        Iterable<Category> categories = categoryService.getAllCategories();

        if (categories == null) {
            return ResponseEntity.notFound().build();
        }

        Iterable<CategoryDtoOut> categoryDtoOuts = StreamSupport.stream(categories.spliterator(), false).map(
                category -> new CategoryDtoOut(
                        category.getId(),
                        category.getName(),
                        category.getDescription()
                )
        ).toList();

        return ResponseEntity.ok(categoryDtoOuts);
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
                category.getDescription()
        );

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
                category.getDescription()
        );

        return ResponseEntity.ok(categoryDtoOut);
    }

    @PostMapping("/bulk-delete")
    public ResponseEntity<Void> deleteCategories(@RequestBody BulkDeleteDto bulkDeleteDto){
        categoryService.bulkDelete(bulkDeleteDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(202).build();
    }
}
