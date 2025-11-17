package mephi.controller;

import lombok.AllArgsConstructor;
import mephi.dto.ModuleDto;
import mephi.service.ModuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class ModuleController {
    private ModuleService moduleService;

    @GetMapping("/modules/get-list")
    public List<ModuleDto> getAllModulesForCourse(@RequestParam("course_id") Long id) {
        return moduleService.getByCourseId(id);
    }

    @GetMapping("/modules/{id}")
    public ModuleDto getModuleById(@PathVariable Long id) {
        return moduleService.getById(id);
    }

    @PostMapping("/modules/create")
    public ModuleDto createModule(@RequestBody ModuleDto moduleDto) {
        return moduleService.createModule(moduleDto);
    }

    @PutMapping("/modules/{id}")
    public ModuleDto updateModule(@PathVariable Long id, @RequestBody ModuleDto moduleDto) {
        return moduleService.updateModule(id, moduleDto);
    }

    @DeleteMapping("/modules/{id}")
    public void deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
    }
}
