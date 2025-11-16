package mephi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseReviewDto {
    private Long userId;
    private Long courseId;
    private int rating;
    private String review;
}
