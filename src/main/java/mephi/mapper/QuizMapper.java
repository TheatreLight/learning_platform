package mephi.mapper;

import mephi.dto.QuizDto;
import mephi.entity.Quiz;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "module", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "quizSubmissions", ignore = true)
    Quiz toEntity(QuizDto quizDto);

    @Mapping(target = "moduleId", source = "module.id")
    QuizDto toDto(Quiz quiz);
}
