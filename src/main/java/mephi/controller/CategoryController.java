package mephi.controller;

import lombok.AllArgsConstructor;
import mephi.dto.CategoryDto;
import mephi.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    @GetMapping("categories/all")
    public List<CategoryDto> getAllCategories() {
        return categoryService.getList();
    }

    @PostMapping("categories/create")
    public CategoryDto createCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }
}
