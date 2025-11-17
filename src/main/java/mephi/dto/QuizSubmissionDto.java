package mephi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class QuizSubmissionDto {
    private Long id;
    private Long quizId;
    private Long studentId;
    private Double score;
    private LocalDateTime takenAt;
}
