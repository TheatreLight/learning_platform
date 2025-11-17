package mephi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mephi.dto.QuestionDto;
import mephi.entity.Question;
import mephi.entity.Quiz;
import mephi.mapper.QuestionMapper;
import mephi.repository.QuestionRepository;
import mephi.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final QuestionMapper questionMapper;

    public List<QuestionDto> getAll() {
        return questionRepository.findAll().stream()
                .map(questionMapper::toDto)
                .collect(Collectors.toList());
    }

    public QuestionDto getById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + id));
        return questionMapper.toDto(question);
    }

    public List<QuestionDto> getByQuizId(Long quizId) {
        List<Question> questions = questionRepository.findByQuizId(quizId);
        return questions.stream()
                .map(questionMapper::toDto)
                .collect(Collectors.toList());
    }

    public QuestionDto create(QuestionDto questionDto) {
        Question question = questionMapper.toEntity(questionDto);
        Quiz quiz = quizRepository.findById(questionDto.getQuizId())
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + questionDto.getQuizId()));
        question.setQuiz(quiz);
        Question saved = questionRepository.save(question);
        return questionMapper.toDto(saved);
    }

    public QuestionDto update(Long id, QuestionDto questionDto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + id));

        question.setText(questionDto.getText());
        question.setType(questionDto.getType());

        if (questionDto.getQuizId() != null && !questionDto.getQuizId().equals(question.getQuiz().getId())) {
            Quiz quiz = quizRepository.findById(questionDto.getQuizId())
                    .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + questionDto.getQuizId()));
            question.setQuiz(quiz);
        }

        Question updated = questionRepository.save(question);
        return questionMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new EntityNotFoundException("Question not found with id: " + id);
        }
        questionRepository.deleteById(id);
    }
}
