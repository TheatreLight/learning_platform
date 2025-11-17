package mephi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mephi.entity.Question;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class QuestionDto {
    private Long id;
    private String text;
    private Question.QuestionType type;
    private Long quizId;
}
