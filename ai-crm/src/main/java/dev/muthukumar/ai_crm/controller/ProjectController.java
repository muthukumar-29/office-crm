package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.Course;
import dev.muthukumar.ai_crm.model.Domain;
import dev.muthukumar.ai_crm.model.Project;
import dev.muthukumar.ai_crm.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<Project> create(@RequestBody Project Project){
        return new ResponseEntity<>(projectService.create(Project), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAll(){
        return new ResponseEntity<>(projectService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Project> getById(@PathVariable Long id){
        Project Project = projectService.getById(id);
        return new ResponseEntity<>(Project, HttpStatus.OK);
    }

    @GetMapping("/domainId/{domainId}")
    public ResponseEntity<List<Project>> getByDomain(@PathVariable Long domainId){
        return new ResponseEntity<>(projectService.getByDomain(domainId), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Project>> getById(@RequestParam int page, @RequestParam int size){
        Page<Project> ProjectPage = projectService.getByPages(page, size);
        return new ResponseEntity<>(ProjectPage, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Project> update(@RequestBody Project Project){
        return new ResponseEntity<>(projectService.update(Project),HttpStatus.OK);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Project> updateById(@PathVariable Long id, @RequestBody Project Project){
        return new ResponseEntity<>(projectService.updateById(id, Project),HttpStatus.OK);
    }

    @DeleteMapping("/id/{id}")
    public void deleteById(@PathVariable Long id){
        projectService.deleteById(id);
    }

    @DeleteMapping
    public void delete(@RequestBody Project Project){
        projectService.delete(Project);
    }
    
}
