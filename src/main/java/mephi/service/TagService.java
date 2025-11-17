package mephi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mephi.dto.TagDto;
import mephi.entity.Tag;
import mephi.mapper.TagMapper;
import mephi.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public List<TagDto> getAll() {
        return tagRepository.findAll().stream()
                .map(tagMapper::toDto)
                .collect(Collectors.toList());
    }

    public TagDto getById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id));
        return tagMapper.toDto(tag);
    }

    public TagDto getByName(String name) {
        Tag tag = tagRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with name: " + name));
        return tagMapper.toDto(tag);
    }

    public TagDto create(TagDto tagDto) {
        Tag tag = tagMapper.toEntity(tagDto);
        Tag saved = tagRepository.save(tag);
        return tagMapper.toDto(saved);
    }

    public TagDto update(Long id, TagDto tagDto) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id));

        tag.setName(tagDto.getName());

        Tag updated = tagRepository.save(tag);
        return tagMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new EntityNotFoundException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }
}
