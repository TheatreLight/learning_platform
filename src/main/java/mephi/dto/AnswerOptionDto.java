package mephi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AnswerOptionDto {
    private Long id;
    private String text;
    private Boolean isCorrect;
    private Long questionId;
}
