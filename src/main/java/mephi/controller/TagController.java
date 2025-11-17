package mephi.controller;

import lombok.AllArgsConstructor;
import mephi.dto.TagDto;
import mephi.service.TagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@AllArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    public List<TagDto> getAllTags() {
        return tagService.getAll();
    }

    @GetMapping("/{id}")
    public TagDto getTagById(@PathVariable Long id) {
        return tagService.getById(id);
    }

    @GetMapping("/name/{name}")
    public TagDto getTagByName(@PathVariable String name) {
        return tagService.getByName(name);
    }

    @PostMapping
    public TagDto createTag(@RequestBody TagDto tagDto) {
        return tagService.create(tagDto);
    }

    @PutMapping("/{id}")
    public TagDto updateTag(@PathVariable Long id, @RequestBody TagDto tagDto) {
        return tagService.update(id, tagDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTag(@PathVariable Long id) {
        tagService.delete(id);
    }
}
