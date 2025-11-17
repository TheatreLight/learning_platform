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
public class AssignmentDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer maxScore;
    private Long lessonId;
}
