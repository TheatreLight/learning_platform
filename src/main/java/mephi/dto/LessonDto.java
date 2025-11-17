package mephi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LessonDto {
    private Long id;
    private String title;
    private String content;
    private String videoUrl;
    private Integer orderIndex;
    private Long moduleId;
}
