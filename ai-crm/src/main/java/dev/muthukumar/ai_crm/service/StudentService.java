package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.model.Student;
import dev.muthukumar.ai_crm.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Student create(Student student){

        return studentRepository.save(student);
    }

    public List<Student> getAll(){
        return studentRepository.findAll();
    }

    public Student getById(Long id){
        return studentRepository.findById(id).orElseThrow(()->new RuntimeException("Student not found"));
    }

    public Student getByStudentId(String studentId){
        return studentRepository.getByStudentId(studentId).orElseThrow(()->new RuntimeException("Student not found"));
    }

    public Page<Student> getByPages(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return studentRepository.findAll(pageable);
    }

    public Student update(Long id, Student student){
        Student existing = getById(id);
        existing.setName(student.getName());
        existing.setEmail(student.getEmail());
        existing.setCollegeName(student.getCollegeName());
        existing.setPhoneNumber(student.getPhoneNumber());
        existing.setRollNumber(student.getRollNumber());
        return studentRepository.save(existing);
    }

    public Student updateByStudentId(String studentId, Student student){
        Student existing = getByStudentId(studentId);
        existing.setName(student.getName());
        existing.setEmail(student.getEmail());
        existing.setCollegeName(student.getCollegeName());
        existing.setPhoneNumber(student.getPhoneNumber());
        existing.setRollNumber(student.getRollNumber());
        return studentRepository.save(existing);
    }

    public void delete(Long id){
        studentRepository.deleteById(id);
    }

    public void deleteByStudentId(String studentId){
        Student student = getByStudentId(studentId);
        studentRepository.delete(student);
    }

}
