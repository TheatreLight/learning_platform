package mephi.controller;

import lombok.AllArgsConstructor;
import mephi.dto.AnswerOptionDto;
import mephi.dto.QuestionDto;
import mephi.dto.QuizDto;
import mephi.dto.QuizSubmissionDto;
import mephi.service.AnswerOptionService;
import mephi.service.QuestionService;
import mephi.service.QuizService;
import mephi.service.QuizSubmissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@AllArgsConstructor
public class QuizController {
    private final QuizService quizService;
    private final QuestionService questionService;
    private final AnswerOptionService answerOptionService;
    private final QuizSubmissionService quizSubmissionService;

    // Quiz endpoints
    @GetMapping
    public List<QuizDto> getAllQuizzes() {
        return quizService.getAll();
    }

    @GetMapping("/{id}")
    public QuizDto getQuizById(@PathVariable Long id) {
        return quizService.getById(id);
    }

    @GetMapping("/module/{moduleId}")
    public QuizDto getQuizByModule(@PathVariable Long moduleId) {
        return quizService.getByModuleId(moduleId);
    }

    @PostMapping
    public QuizDto createQuiz(@RequestBody QuizDto quizDto) {
        return quizService.create(quizDto);
    }

    @PutMapping("/{id}")
    public QuizDto updateQuiz(@PathVariable Long id, @RequestBody QuizDto quizDto) {
        return quizService.update(id, quizDto);
    }

    @DeleteMapping("/{id}")
    public void deleteQuiz(@PathVariable Long id) {
        quizService.delete(id);
    }

    // Question endpoints
    @GetMapping("/{quizId}/questions")
    public List<QuestionDto> getQuestionsByQuiz(@PathVariable Long quizId) {
        return questionService.getByQuizId(quizId);
    }

    @GetMapping("/questions/{id}")
    public QuestionDto getQuestionById(@PathVariable Long id) {
        return questionService.getById(id);
    }

    @PostMapping("/questions")
    public QuestionDto createQuestion(@RequestBody QuestionDto questionDto) {
        return questionService.create(questionDto);
    }

    @PutMapping("/questions/{id}")
    public QuestionDto updateQuestion(@PathVariable Long id, @RequestBody QuestionDto questionDto) {
        return questionService.update(id, questionDto);
    }

    @DeleteMapping("/questions/{id}")
    public void deleteQuestion(@PathVariable Long id) {
        questionService.delete(id);
    }

    // Answer Option endpoints
    @GetMapping("/questions/{questionId}/options")
    public List<AnswerOptionDto> getOptionsByQuestion(@PathVariable Long questionId) {
        return answerOptionService.getByQuestionId(questionId);
    }

    @GetMapping("/options/{id}")
    public AnswerOptionDto getOptionById(@PathVariable Long id) {
        return answerOptionService.getById(id);
    }

    @PostMapping("/options")
    public AnswerOptionDto createOption(@RequestBody AnswerOptionDto answerOptionDto) {
        return answerOptionService.create(answerOptionDto);
    }

    @PutMapping("/options/{id}")
    public AnswerOptionDto updateOption(@PathVariable Long id, @RequestBody AnswerOptionDto answerOptionDto) {
        return answerOptionService.update(id, answerOptionDto);
    }

    @DeleteMapping("/options/{id}")
    public void deleteOption(@PathVariable Long id) {
        answerOptionService.delete(id);
    }

    // Quiz Submission endpoints
    @GetMapping("/{quizId}/submissions")
    public List<QuizSubmissionDto> getSubmissionsByQuiz(@PathVariable Long quizId) {
        return quizSubmissionService.getByQuizId(quizId);
    }

    @GetMapping("/submissions/{id}")
    public QuizSubmissionDto getSubmissionById(@PathVariable Long id) {
        return quizSubmissionService.getById(id);
    }

    @GetMapping("/submissions/student/{studentId}")
    public List<QuizSubmissionDto> getSubmissionsByStudent(@PathVariable Long studentId) {
        return quizSubmissionService.getByStudentId(studentId);
    }

    @PostMapping("/submissions")
    public QuizSubmissionDto createSubmission(@RequestBody QuizSubmissionDto quizSubmissionDto) {
        return quizSubmissionService.create(quizSubmissionDto);
    }

    @PutMapping("/submissions/{id}")
    public QuizSubmissionDto updateSubmission(@PathVariable Long id, @RequestBody QuizSubmissionDto quizSubmissionDto) {
        return quizSubmissionService.update(id, quizSubmissionDto);
    }

    @DeleteMapping("/submissions/{id}")
    public void deleteSubmission(@PathVariable Long id) {
        quizSubmissionService.delete(id);
    }
}
