package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.Course;
import dev.muthukumar.ai_crm.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<Course> create(@RequestBody Course course){
        return new ResponseEntity<>(courseService.create(course), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAll(){
        return new ResponseEntity<>(courseService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Course> getById(@PathVariable Long id){
        return new ResponseEntity<>(courseService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/courseId/{courseId}")
    public ResponseEntity<Course> getByCourseId(@PathVariable String courseId){
        return new ResponseEntity<>(courseService.getByCourseId(courseId), HttpStatus.OK);
    }

    @GetMapping("/domainId/{domainId}")
    public ResponseEntity<List<Course>> getByDomainId(@PathVariable Long domainId){
        return new ResponseEntity<>(courseService.getByDomain(domainId), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Course>> getByPages(@RequestParam int page, @RequestParam int size){
        return new ResponseEntity<>(courseService.getByPages(page, size), HttpStatus.OK);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Course> update(@PathVariable Long id, @RequestBody Course course){
        return new ResponseEntity<>(courseService.update(id, course), HttpStatus.OK);
    }

    @PutMapping("/courseId/{courseId}")
    public ResponseEntity<Course> updateByCourseId(@PathVariable String courseId, @RequestBody Course course){
        return new ResponseEntity<>(courseService.updateByCourseId(courseId, course), HttpStatus.OK);
    }

    @DeleteMapping("/id/{id}")
    public void delete(@PathVariable Long id){
        courseService.deleteById(id);
    }

}
