package mephi.mapper;

import mephi.dto.ModuleDto;
import mephi.entity.Module;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModuleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "quiz", ignore = true)
    mephi.entity.Module toEntity(ModuleDto moduleDto);

    @Mapping(target = "courseId", source = "course.id")
    ModuleDto toDto(Module module);
}
