package mephi.mapper;

import mephi.dto.QuestionDto;
import mephi.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quiz", ignore = true)
    @Mapping(target = "options", ignore = true)
    Question toEntity(QuestionDto questionDto);

    @Mapping(target = "quizId", source = "quiz.id")
    QuestionDto toDto(Question question);
}
