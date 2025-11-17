package mephi.mapper;

import mephi.dto.LessonDto;
import mephi.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "module", ignore = true)
    @Mapping(target = "assignments", ignore = true)
    Lesson toEntity(LessonDto lessonDto);

    @Mapping(target = "moduleId", source = "module.id")
    LessonDto toDto(Lesson lesson);
}
