package mephi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mephi.dto.ModuleDto;
import mephi.entity.Course;
import mephi.entity.Module;
import mephi.mapper.ModuleMapper;
import mephi.repository.CourseRepository;
import mephi.repository.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ModuleService {
    private ModuleRepository moduleRepository;
    private CourseRepository courseRepository;
    private ModuleMapper moduleMapper;

    public List<ModuleDto> getByCourseId(Long id) {
        List<Module> modules = moduleRepository.findByCourseId(id);
        List<ModuleDto> resultList = new ArrayList<>();
        for (Module item : modules) {
            resultList.add(moduleMapper.toDto(item));
        }
        return resultList;
    }

    public ModuleDto getById(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + id));
        return moduleMapper.toDto(module);
    }

    public ModuleDto createModule(ModuleDto moduleDto) {
        Module module = moduleMapper.toEntity(moduleDto);
        Course course = courseRepository.findById(moduleDto.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + moduleDto.getCourseId()));
        module.setCourse(course);
        var saved = moduleRepository.save(module);
        return moduleMapper.toDto(saved);
    }

    public ModuleDto updateModule(Long id, ModuleDto moduleDto) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + id));

        module.setTitle(moduleDto.getTitle());
        module.setOrderIndex(moduleDto.getOrderIndex());
        module.setDescription(moduleDto.getDescription());

        if (moduleDto.getCourseId() != null && !moduleDto.getCourseId().equals(module.getCourse().getId())) {
            Course course = courseRepository.findById(moduleDto.getCourseId())
                    .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + moduleDto.getCourseId()));
            module.setCourse(course);
        }

        Module updated = moduleRepository.save(module);
        return moduleMapper.toDto(updated);
    }

    public void deleteModule(Long id) {
        if (!moduleRepository.existsById(id)) {
            throw new EntityNotFoundException("Module not found with id: " + id);
        }
        moduleRepository.deleteById(id);
    }
}
