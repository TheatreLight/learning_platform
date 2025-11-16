package mephi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mephi.EnrollStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDto {
    private Long userId;
    private Long courseId;
    private LocalDate enrollDate;
    private EnrollStatus status;
}
