package mephi.mapper;

import mephi.dto.CourseReviewDto;
import mephi.entity.CourseReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseReviewMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "user", ignore = true)
    CourseReview toEntity(CourseReviewDto courseReviewDto);

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "userId", source = "user.id")
    CourseReviewDto toDto(CourseReview courseReview);
}
