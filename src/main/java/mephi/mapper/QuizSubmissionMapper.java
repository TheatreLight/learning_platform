package mephi.mapper;

import mephi.dto.QuizSubmissionDto;
import mephi.entity.QuizSubmission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuizSubmissionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quiz", ignore = true)
    @Mapping(target = "student", ignore = true)
    QuizSubmission toEntity(QuizSubmissionDto quizSubmissionDto);

    @Mapping(target = "quizId", source = "quiz.id")
    @Mapping(target = "studentId", source = "student.id")
    QuizSubmissionDto toDto(QuizSubmission quizSubmission);
}
