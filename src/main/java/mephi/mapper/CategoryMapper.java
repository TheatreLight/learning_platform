package mephi.mapper;

import mephi.dto.CategoryDto;
import mephi.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    Category toEntity(CategoryDto categoryDto);
    CategoryDto toDto(Category category);
}
