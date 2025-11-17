package mephi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mephi.dto.QuizSubmissionDto;
import mephi.entity.Quiz;
import mephi.entity.QuizSubmission;
import mephi.entity.User;
import mephi.mapper.QuizSubmissionMapper;
import mephi.repository.QuizRepository;
import mephi.repository.QuizSubmissionRepository;
import mephi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class QuizSubmissionService {
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuizSubmissionMapper quizSubmissionMapper;

    public List<QuizSubmissionDto> getAll() {
        return quizSubmissionRepository.findAll().stream()
                .map(quizSubmissionMapper::toDto)
                .collect(Collectors.toList());
    }

    public QuizSubmissionDto getById(Long id) {
        QuizSubmission quizSubmission = quizSubmissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("QuizSubmission not found with id: " + id));
        return quizSubmissionMapper.toDto(quizSubmission);
    }

    public List<QuizSubmissionDto> getByQuizId(Long quizId) {
        List<QuizSubmission> submissions = quizSubmissionRepository.findByQuizId(quizId);
        return submissions.stream()
                .map(quizSubmissionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<QuizSubmissionDto> getByStudentId(Long studentId) {
        List<QuizSubmission> submissions = quizSubmissionRepository.findByStudentId(studentId);
        return submissions.stream()
                .map(quizSubmissionMapper::toDto)
                .collect(Collectors.toList());
    }

    public QuizSubmissionDto create(QuizSubmissionDto quizSubmissionDto) {
        QuizSubmission quizSubmission = quizSubmissionMapper.toEntity(quizSubmissionDto);

        Quiz quiz = quizRepository.findById(quizSubmissionDto.getQuizId())
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + quizSubmissionDto.getQuizId()));
        User student = userRepository.findById(quizSubmissionDto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + quizSubmissionDto.getStudentId()));

        quizSubmission.setQuiz(quiz);
        quizSubmission.setStudent(student);
        quizSubmission.setTakenAt(LocalDateTime.now());

        QuizSubmission saved = quizSubmissionRepository.save(quizSubmission);
        return quizSubmissionMapper.toDto(saved);
    }

    public QuizSubmissionDto update(Long id, QuizSubmissionDto quizSubmissionDto) {
        QuizSubmission quizSubmission = quizSubmissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("QuizSubmission not found with id: " + id));

        quizSubmission.setScore(quizSubmissionDto.getScore());

        QuizSubmission updated = quizSubmissionRepository.save(quizSubmission);
        return quizSubmissionMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!quizSubmissionRepository.existsById(id)) {
            throw new EntityNotFoundException("QuizSubmission not found with id: " + id);
        }
        quizSubmissionRepository.deleteById(id);
    }
}
