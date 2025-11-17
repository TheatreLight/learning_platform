package mephi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mephi.dto.QuizDto;
import mephi.entity.Module;
import mephi.entity.Quiz;
import mephi.mapper.QuizMapper;
import mephi.repository.ModuleRepository;
import mephi.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class QuizService {
    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final QuizMapper quizMapper;

    public List<QuizDto> getAll() {
        return quizRepository.findAll().stream()
                .map(quizMapper::toDto)
                .collect(Collectors.toList());
    }

    public QuizDto getById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + id));
        return quizMapper.toDto(quiz);
    }

    public QuizDto getByModuleId(Long moduleId) {
        Quiz quiz = quizRepository.findByModuleId(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found for module id: " + moduleId));
        return quizMapper.toDto(quiz);
    }

    public QuizDto create(QuizDto quizDto) {
        Quiz quiz = quizMapper.toEntity(quizDto);

        if (quizDto.getModuleId() != null) {
            Module module = moduleRepository.findById(quizDto.getModuleId())
                    .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + quizDto.getModuleId()));
            quiz.setModule(module);
        }

        Quiz saved = quizRepository.save(quiz);
        return quizMapper.toDto(saved);
    }

    public QuizDto update(Long id, QuizDto quizDto) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + id));

        quiz.setTitle(quizDto.getTitle());
        quiz.setTimeLimit(quizDto.getTimeLimit());

        if (quizDto.getModuleId() != null) {
            Module module = moduleRepository.findById(quizDto.getModuleId())
                    .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + quizDto.getModuleId()));
            quiz.setModule(module);
        }

        Quiz updated = quizRepository.save(quiz);
        return quizMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new EntityNotFoundException("Quiz not found with id: " + id);
        }
        quizRepository.deleteById(id);
    }
}
