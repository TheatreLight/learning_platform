package mephi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ModuleDto {
    private Long id;
    private String title;
    private int orderIndex;
    private String description;
    private Long courseId;
}
