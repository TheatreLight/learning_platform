package mephi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mephi.dto.SubmissionDto;
import mephi.entity.Assignment;
import mephi.entity.Submission;
import mephi.entity.User;
import mephi.mapper.SubmissionMapper;
import mephi.repository.AssignmentRepository;
import mephi.repository.SubmissionRepository;
import mephi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final SubmissionMapper submissionMapper;

    public List<SubmissionDto> getAll() {
        return submissionRepository.findAll().stream()
                .map(submissionMapper::toDto)
                .collect(Collectors.toList());
    }

    public SubmissionDto getById(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found with id: " + id));
        return submissionMapper.toDto(submission);
    }

    public List<SubmissionDto> getByAssignmentId(Long assignmentId) {
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);
        return submissions.stream()
                .map(submissionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SubmissionDto> getByStudentId(Long studentId) {
        List<Submission> submissions = submissionRepository.findByStudentId(studentId);
        return submissions.stream()
                .map(submissionMapper::toDto)
                .collect(Collectors.toList());
    }

    public SubmissionDto create(SubmissionDto submissionDto) {
        Submission submission = submissionMapper.toEntity(submissionDto);

        Assignment assignment = assignmentRepository.findById(submissionDto.getAssignmentId())
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found with id: " + submissionDto.getAssignmentId()));
        User student = userRepository.findById(submissionDto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + submissionDto.getStudentId()));

        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmittedAt(LocalDateTime.now());

        Submission saved = submissionRepository.save(submission);
        return submissionMapper.toDto(saved);
    }

    public SubmissionDto update(Long id, SubmissionDto submissionDto) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found with id: " + id));

        submission.setContent(submissionDto.getContent());
        submission.setScore(submissionDto.getScore());
        submission.setFeedback(submissionDto.getFeedback());

        Submission updated = submissionRepository.save(submission);
        return submissionMapper.toDto(updated);
    }

    public SubmissionDto grade(Long id, Integer score, String feedback) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found with id: " + id));

        submission.setScore(score);
        submission.setFeedback(feedback);

        Submission updated = submissionRepository.save(submission);
        return submissionMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!submissionRepository.existsById(id)) {
            throw new EntityNotFoundException("Submission not found with id: " + id);
        }
        submissionRepository.deleteById(id);
    }
}
