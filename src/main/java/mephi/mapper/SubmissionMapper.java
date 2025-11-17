package mephi.mapper;

import mephi.dto.SubmissionDto;
import mephi.entity.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignment", ignore = true)
    @Mapping(target = "student", ignore = true)
    Submission toEntity(SubmissionDto submissionDto);

    @Mapping(target = "assignmentId", source = "assignment.id")
    @Mapping(target = "studentId", source = "student.id")
    SubmissionDto toDto(Submission submission);
}
