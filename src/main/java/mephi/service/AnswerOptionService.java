package mephi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mephi.dto.AnswerOptionDto;
import mephi.entity.AnswerOption;
import mephi.entity.Question;
import mephi.mapper.AnswerOptionMapper;
import mephi.repository.AnswerOptionRepository;
import mephi.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class AnswerOptionService {
    private final AnswerOptionRepository answerOptionRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionMapper answerOptionMapper;

    public List<AnswerOptionDto> getAll() {
        return answerOptionRepository.findAll().stream()
                .map(answerOptionMapper::toDto)
                .collect(Collectors.toList());
    }

    public AnswerOptionDto getById(Long id) {
        AnswerOption answerOption = answerOptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AnswerOption not found with id: " + id));
        return answerOptionMapper.toDto(answerOption);
    }

    public List<AnswerOptionDto> getByQuestionId(Long questionId) {
        List<AnswerOption> answerOptions = answerOptionRepository.findByQuestionId(questionId);
        return answerOptions.stream()
                .map(answerOptionMapper::toDto)
                .collect(Collectors.toList());
    }

    public AnswerOptionDto create(AnswerOptionDto answerOptionDto) {
        AnswerOption answerOption = answerOptionMapper.toEntity(answerOptionDto);
        Question question = questionRepository.findById(answerOptionDto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + answerOptionDto.getQuestionId()));
        answerOption.setQuestion(question);
        AnswerOption saved = answerOptionRepository.save(answerOption);
        return answerOptionMapper.toDto(saved);
    }

    public AnswerOptionDto update(Long id, AnswerOptionDto answerOptionDto) {
        AnswerOption answerOption = answerOptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AnswerOption not found with id: " + id));

        answerOption.setText(answerOptionDto.getText());
        answerOption.setIsCorrect(answerOptionDto.getIsCorrect());

        if (answerOptionDto.getQuestionId() != null && !answerOptionDto.getQuestionId().equals(answerOption.getQuestion().getId())) {
            Question question = questionRepository.findById(answerOptionDto.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + answerOptionDto.getQuestionId()));
            answerOption.setQuestion(question);
        }

        AnswerOption updated = answerOptionRepository.save(answerOption);
        return answerOptionMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!answerOptionRepository.existsById(id)) {
            throw new EntityNotFoundException("AnswerOption not found with id: " + id);
        }
        answerOptionRepository.deleteById(id);
    }
}
