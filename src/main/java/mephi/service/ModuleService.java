package mephi.service;

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

    public ModuleDto createModule(ModuleDto moduleDto) {
        Module module = moduleMapper.toEntity(moduleDto);
        Course course = courseRepository.findById(moduleDto.getCourseId())
                .orElseThrow();
        module.setCourse(course);
        var saved = moduleRepository.save(module);
        return moduleMapper.toDto(saved);
    }
}
