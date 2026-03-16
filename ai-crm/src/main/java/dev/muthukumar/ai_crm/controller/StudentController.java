package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.Student;
import dev.muthukumar.ai_crm.repository.StudentRepository;
import dev.muthukumar.ai_crm.service.StudentService;
import lombok.RequiredArgsConstructor;
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
        long time = System.currentTimeMillis() % 100000000;
        String timePart = Long.toString(time, 36);
        int random = ThreadLocalRandom.current().nextInt(36 * 36);
        String randomPart = Integer.toString(random, 36);
        return "AI-EDU-" + timePart + randomPart;
    }

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new RuntimeException("Student with this email already exists");
        }
        student.setStudentId(generateStudentId());
        return new ResponseEntity<>(studentService.create(student), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAll() {
        return new ResponseEntity<>(studentService.getAll(), HttpStatus.OK);
    }

    // ✅ New frontend calls GET /students/{id} — kept for compatibility
    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        return new ResponseEntity<>(studentService.getById(id), HttpStatus.OK);
    }

    // Keep old path too for backward compat
    @GetMapping("/id/{id}")
    public ResponseEntity<Student> getByIdLegacy(@PathVariable Long id) {
        return new ResponseEntity<>(studentService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/studentId/{studentId}")
    public ResponseEntity<Student> getByStudentId(@PathVariable String studentId) {
        return new ResponseEntity<>(studentService.getByStudentId(studentId), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Student>> getByPage(@RequestParam int page, @RequestParam int size) {
        return new ResponseEntity<>(studentService.getByPages(page, size), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Student>> search(@RequestParam String q) {
        List<Student> all = studentService.getAll();
        String lower = q.toLowerCase();
        List<Student> results = all.stream().filter(s ->
                (s.getName() != null && s.getName().toLowerCase().contains(lower)) ||
                        (s.getEmail() != null && s.getEmail().toLowerCase().contains(lower)) ||
                        (s.getRollNo() != null && s.getRollNo().toLowerCase().contains(lower))
        ).toList();
        return ResponseEntity.ok(results);
    }

    // ✅ Fixed: new frontend uses PUT /students/{id} (no /id/ prefix)
    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student) {
        return new ResponseEntity<>(studentService.update(id, student), HttpStatus.OK);
    }

    // Keep legacy path too
    @PutMapping("/id/{id}")
    public ResponseEntity<Student> updateLegacy(@PathVariable Long id, @RequestBody Student student) {
        return new ResponseEntity<>(studentService.update(id, student), HttpStatus.OK);
    }

    // ✅ Fixed: new frontend uses DELETE /students/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Keep legacy path too
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteLegacy(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/studentId/{studentId}")
    public void deleteByStudentId(@PathVariable String studentId) {
        studentService.deleteByStudentId(studentId);
    }
}