package mephi.mapper;

import mephi.dto.ProfileDto;
import mephi.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileDto toDto(Profile profile);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Profile toEntity(ProfileDto dto);
}
