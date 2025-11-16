package mephi.service;

import lombok.AllArgsConstructor;
import mephi.dto.CategoryDto;
import mephi.entity.Category;
import mephi.mapper.CategoryMapper;
import mephi.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private CategoryRepository categoryRepository;
    private CategoryMapper categoryMapper;
    public List<CategoryDto> getList() {
        var categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (var item : categories) {
            var dto = categoryMapper.toDto(item);
            categoryDtoList.add(dto);
        }
        return categoryDtoList;
    }

    public CategoryDto createCategory(@RequestBody CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }
}
