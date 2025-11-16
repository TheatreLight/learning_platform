package mephi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class ProfileDto {
    private String bio;
    private String avatar_url;
}
