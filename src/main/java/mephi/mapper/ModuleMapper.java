package mephi.mapper;

import mephi.dto.ModuleDto;
import mephi.entity.Module;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModuleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    public mephi.entity.Module toEntity(ModuleDto moduleDto);

    @Mapping(target = "courseId", source = "course.id")
    public ModuleDto toDto(Module module);
}
