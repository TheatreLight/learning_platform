package mephi.mapper;

import mephi.dto.AssignmentDto;
import mephi.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "submissions", ignore = true)
    Assignment toEntity(AssignmentDto assignmentDto);

    @Mapping(target = "lessonId", source = "lesson.id")
    AssignmentDto toDto(Assignment assignment);
}
