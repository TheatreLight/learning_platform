package mephi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mephi.dto.LessonDto;
import mephi.entity.Lesson;
import mephi.entity.Module;
import mephi.mapper.LessonMapper;
import mephi.repository.LessonRepository;
import mephi.repository.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final LessonMapper lessonMapper;

    public List<LessonDto> getAll() {
        return lessonRepository.findAll().stream()
                .map(lessonMapper::toDto)
                .collect(Collectors.toList());
    }

    public LessonDto getById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + id));
        return lessonMapper.toDto(lesson);
    }

    public List<LessonDto> getByModuleId(Long moduleId) {
        List<Lesson> lessons = lessonRepository.findByModuleId(moduleId);
        return lessons.stream()
                .map(lessonMapper::toDto)
                .collect(Collectors.toList());
    }

    public LessonDto create(LessonDto lessonDto) {
        Lesson lesson = lessonMapper.toEntity(lessonDto);
        Module module = moduleRepository.findById(lessonDto.getModuleId())
                .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + lessonDto.getModuleId()));
        lesson.setModule(module);
        Lesson saved = lessonRepository.save(lesson);
        return lessonMapper.toDto(saved);
    }

    public LessonDto update(Long id, LessonDto lessonDto) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + id));

        lesson.setTitle(lessonDto.getTitle());
        lesson.setContent(lessonDto.getContent());
        lesson.setVideoUrl(lessonDto.getVideoUrl());
        lesson.setOrderIndex(lessonDto.getOrderIndex());

        if (lessonDto.getModuleId() != null && !lessonDto.getModuleId().equals(lesson.getModule().getId())) {
            Module module = moduleRepository.findById(lessonDto.getModuleId())
                    .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + lessonDto.getModuleId()));
            lesson.setModule(module);
        }

        Lesson updated = lessonRepository.save(lesson);
        return lessonMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new EntityNotFoundException("Lesson not found with id: " + id);
        }
        lessonRepository.deleteById(id);
    }
}
