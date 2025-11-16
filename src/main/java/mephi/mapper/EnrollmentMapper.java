package mephi.mapper;

import mephi.dto.EnrollmentDto;
import mephi.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "course", ignore = true)
    Enrollment toEntity(EnrollmentDto eDto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "courseId", source = "course.id")
    EnrollmentDto toDto(Enrollment e);
}
