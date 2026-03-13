package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.Course;
import dev.muthukumar.ai_crm.model.Intern;
import dev.muthukumar.ai_crm.service.InternService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/intern")
@RequiredArgsConstructor
public class InternController {

    private final InternService internService;

    @PostMapping
    public ResponseEntity<Intern> create(@RequestBody Intern intern){
        return new ResponseEntity<>(internService.create(intern),HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Intern>> getAll(){
        return new ResponseEntity<>(internService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Intern> getById(@PathVariable Long id){
        Intern intern = internService.getById(id);
        return new ResponseEntity<>(intern, HttpStatus.OK);
    }

    @GetMapping("/domainId/{domainId}")
    public ResponseEntity<List<Intern>> getByDomainId(@PathVariable Long domainId){
        return new ResponseEntity<>(internService.getByDomain(domainId), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Intern>> getById(@RequestParam int page, @RequestParam int size){
        Page<Intern> internPage = internService.getByPages(page, size);
        return new ResponseEntity<>(internPage, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Intern> update(@RequestBody Intern intern){
        return new ResponseEntity<>(internService.update(intern),HttpStatus.OK);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Intern> updateById(@PathVariable Long id, @RequestBody Intern intern){
        return new ResponseEntity<>(internService.updateById(id, intern),HttpStatus.OK);
    }

    @DeleteMapping("/id/{id}")
    public void deleteById(@PathVariable Long id){
        internService.deleteById(id);
    }

    @DeleteMapping
    public void delete(@RequestBody Intern intern){
        internService.delete(intern);
    }

}
