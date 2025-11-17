package mephi.mapper;

import mephi.dto.TagDto;
import mephi.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    Tag toEntity(TagDto tagDto);

    TagDto toDto(Tag tag);
}
