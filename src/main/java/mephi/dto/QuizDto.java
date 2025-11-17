package mephi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class QuizDto {
    private Long id;
    private String title;
    private Integer timeLimit;
    private Long moduleId;
}
