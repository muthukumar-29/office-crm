package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.Student;
import dev.muthukumar.ai_crm.repository.StudentRepository;
import dev.muthukumar.ai_crm.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentRepository studentRepository;

    public static String generateStudentId() {
        long time = System.currentTimeMillis() % 100000000; // last 8 digits of time
        String timePart = Long.toString(time, 36); // base36 for compactness

        int random = ThreadLocalRandom.current().nextInt(36 * 36); // 2 chars
        String randomPart = Integer.toString(random, 36);

        return "AI-EDU-" + timePart + randomPart; // max ~10 chars
    }

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new RuntimeException("User with this Email already exist");
        }

        student.setStudentId(generateStudentId());

        return new ResponseEntity<>(studentService.create(student), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAll() {
        return new ResponseEntity<>(studentService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        return new ResponseEntity<>(studentService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/studentId/{studentId}")
    public ResponseEntity<Student> getByStudentId(@PathVariable String studentId) {
        return new ResponseEntity<>(studentService.getByStudentId(studentId), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Student>> getByStudentId(@RequestParam int page, @RequestParam int size) {
        Page<Student> studentPage = studentService.getByPages(page, size);
        return new ResponseEntity<>(studentPage, HttpStatus.OK);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student) {
        return new ResponseEntity<>(studentService.update(id, student), HttpStatus.OK);
    }

    @PutMapping("/studentId/{studentId}")
    public ResponseEntity<Student> updateByStudentId(@PathVariable String studentId, @RequestBody Student student) {
        return new ResponseEntity<>(studentService.updateByStudentId(studentId, student), HttpStatus.OK);
    }

    @DeleteMapping("/id/{id}")
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }

    @DeleteMapping("/studentId/{studentId}")
    public void deleteByStudentId(@PathVariable String studentId) {
        studentService.deleteByStudentId(studentId);
    }

}
