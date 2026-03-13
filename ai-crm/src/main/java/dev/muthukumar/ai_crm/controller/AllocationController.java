package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.Allocation;
import dev.muthukumar.ai_crm.service.AllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/allocate")
@RequiredArgsConstructor
public class AllocationController {

    private final AllocationService allocationService;

    @PostMapping
    public ResponseEntity<Allocation> create(@RequestBody Allocation allocation){
        return new ResponseEntity<>(allocationService.create(allocation), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Allocation>> getAll(){
        return new ResponseEntity<>(allocationService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Allocation>> getByPages(@RequestParam int page, @RequestParam int size){
        return new ResponseEntity<>(allocationService.getByPages(page, size), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Allocation> getById(@PathVariable Long id){
        return new ResponseEntity<>(allocationService.getById(id), HttpStatus.OK);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Allocation> update(@PathVariable Long id, @RequestBody Allocation allocation){
        return new ResponseEntity<>(allocationService.update(id, allocation), HttpStatus.OK);
    }

    @DeleteMapping("/id/{id}")
    public void delete(@PathVariable Long id){
        allocationService.delete(id);
    }

}
