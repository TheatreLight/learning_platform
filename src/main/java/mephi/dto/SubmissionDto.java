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
public class SubmissionDto {
    private Long id;
    private Long assignmentId;
    private Long studentId;
    private LocalDateTime submittedAt;
    private String content;
    private Integer score;
    private String feedback;
}
