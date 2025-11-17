package mephi.mapper;

import mephi.dto.UserDto;
import mephi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProfileMapper.class)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "submissions", ignore = true)
    @Mapping(target = "quizSubmissions", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    User toEntity(UserDto userDto);

    UserDto toDto(User user);
}
