package mephi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mephi.entity.Course;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CourseDto {
    private Long id;
    private String title;
    private String description;
    private int duration;
    private Long categoryId;
    private Long teacherId;
}
