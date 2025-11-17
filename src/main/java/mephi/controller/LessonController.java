package mephi.controller;

import lombok.AllArgsConstructor;
import mephi.dto.AssignmentDto;
import mephi.dto.LessonDto;
import mephi.dto.SubmissionDto;
import mephi.service.AssignmentService;
import mephi.service.LessonService;
import mephi.service.SubmissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@AllArgsConstructor
public class LessonController {
    private final LessonService lessonService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    // Lesson endpoints
    @GetMapping
    public List<LessonDto> getAllLessons() {
        return lessonService.getAll();
    }

    @GetMapping("/{id}")
    public LessonDto getLessonById(@PathVariable Long id) {
        return lessonService.getById(id);
    }

    @GetMapping("/module/{moduleId}")
    public List<LessonDto> getLessonsByModule(@PathVariable Long moduleId) {
        return lessonService.getByModuleId(moduleId);
    }

    @PostMapping
    public LessonDto createLesson(@RequestBody LessonDto lessonDto) {
        return lessonService.create(lessonDto);
    }

    @PutMapping("/{id}")
    public LessonDto updateLesson(@PathVariable Long id, @RequestBody LessonDto lessonDto) {
        return lessonService.update(id, lessonDto);
    }

    @DeleteMapping("/{id}")
    public void deleteLesson(@PathVariable Long id) {
        lessonService.delete(id);
    }

    // Assignment endpoints
    @GetMapping("/{lessonId}/assignments")
    public List<AssignmentDto> getAssignmentsByLesson(@PathVariable Long lessonId) {
        return assignmentService.getByLessonId(lessonId);
    }

    @GetMapping("/assignments/{id}")
    public AssignmentDto getAssignmentById(@PathVariable Long id) {
        return assignmentService.getById(id);
    }

    @PostMapping("/assignments")
    public AssignmentDto createAssignment(@RequestBody AssignmentDto assignmentDto) {
        return assignmentService.create(assignmentDto);
    }

    @PutMapping("/assignments/{id}")
    public AssignmentDto updateAssignment(@PathVariable Long id, @RequestBody AssignmentDto assignmentDto) {
        return assignmentService.update(id, assignmentDto);
    }

    @DeleteMapping("/assignments/{id}")
    public void deleteAssignment(@PathVariable Long id) {
        assignmentService.delete(id);
    }

    // Submission endpoints
    @GetMapping("/assignments/{assignmentId}/submissions")
    public List<SubmissionDto> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        return submissionService.getByAssignmentId(assignmentId);
    }

    @GetMapping("/submissions/{id}")
    public SubmissionDto getSubmissionById(@PathVariable Long id) {
        return submissionService.getById(id);
    }

    @PostMapping("/submissions")
    public SubmissionDto createSubmission(@RequestBody SubmissionDto submissionDto) {
        return submissionService.create(submissionDto);
    }

    @PutMapping("/submissions/{id}")
    public SubmissionDto updateSubmission(@PathVariable Long id, @RequestBody SubmissionDto submissionDto) {
        return submissionService.update(id, submissionDto);
    }

    @PutMapping("/submissions/{id}/grade")
    public SubmissionDto gradeSubmission(@PathVariable Long id, @RequestParam Integer score, @RequestParam(required = false) String feedback) {
        return submissionService.grade(id, score, feedback);
    }

    @DeleteMapping("/submissions/{id}")
    public void deleteSubmission(@PathVariable Long id) {
        submissionService.delete(id);
    }
}
