package mephi.mapper;

import mephi.dto.AnswerOptionDto;
import mephi.entity.AnswerOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnswerOptionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    AnswerOption toEntity(AnswerOptionDto answerOptionDto);

    @Mapping(target = "questionId", source = "question.id")
    AnswerOptionDto toDto(AnswerOption answerOption);
}
