package mephi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mephi.dto.AssignmentDto;
import mephi.entity.Assignment;
import mephi.entity.Lesson;
import mephi.mapper.AssignmentMapper;
import mephi.repository.AssignmentRepository;
import mephi.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;
    private final AssignmentMapper assignmentMapper;

    public List<AssignmentDto> getAll() {
        return assignmentRepository.findAll().stream()
                .map(assignmentMapper::toDto)
                .collect(Collectors.toList());
    }

    public AssignmentDto getById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found with id: " + id));
        return assignmentMapper.toDto(assignment);
    }

    public List<AssignmentDto> getByLessonId(Long lessonId) {
        List<Assignment> assignments = assignmentRepository.findByLessonId(lessonId);
        return assignments.stream()
                .map(assignmentMapper::toDto)
                .collect(Collectors.toList());
    }

    public AssignmentDto create(AssignmentDto assignmentDto) {
        Assignment assignment = assignmentMapper.toEntity(assignmentDto);
        Lesson lesson = lessonRepository.findById(assignmentDto.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + assignmentDto.getLessonId()));
        assignment.setLesson(lesson);
        Assignment saved = assignmentRepository.save(assignment);
        return assignmentMapper.toDto(saved);
    }

    public AssignmentDto update(Long id, AssignmentDto assignmentDto) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found with id: " + id));

        assignment.setTitle(assignmentDto.getTitle());
        assignment.setDescription(assignmentDto.getDescription());
        assignment.setDueDate(assignmentDto.getDueDate());
        assignment.setMaxScore(assignmentDto.getMaxScore());

        if (assignmentDto.getLessonId() != null && !assignmentDto.getLessonId().equals(assignment.getLesson().getId())) {
            Lesson lesson = lessonRepository.findById(assignmentDto.getLessonId())
                    .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + assignmentDto.getLessonId()));
            assignment.setLesson(lesson);
        }

        Assignment updated = assignmentRepository.save(assignment);
        return assignmentMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Assignment not found with id: " + id);
        }
        assignmentRepository.deleteById(id);
    }
}
