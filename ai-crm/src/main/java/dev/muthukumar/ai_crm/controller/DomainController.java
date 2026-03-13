package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.Category;
import dev.muthukumar.ai_crm.model.Domain;
import dev.muthukumar.ai_crm.service.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domain")
@RequiredArgsConstructor
public class DomainController {

    private final DomainService domainService;

    @PostMapping
    public ResponseEntity<Domain> create(@RequestBody Domain domain){
        return new ResponseEntity<>(domainService.create(domain), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Domain>> getAll(){
        return new ResponseEntity<>(domainService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Domain> getById(@PathVariable Long id){
        return new ResponseEntity<>(domainService.getById(id), HttpStatus.OK);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Domain> update(@PathVariable Long id, @RequestBody Domain domain){
        return new ResponseEntity<>(domainService.update(id,domain), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        domainService.delete(id);
    }

}
