package mephi.mapper;

import mephi.dto.CourseDto;
import mephi.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "modules", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Course toEntity(CourseDto courseDto);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "teacherId", source = "teacher.id")
    CourseDto toDto(Course course);
}
