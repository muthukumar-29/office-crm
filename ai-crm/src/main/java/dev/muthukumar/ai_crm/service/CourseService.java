package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.model.Course;
import dev.muthukumar.ai_crm.model.Intern;
import dev.muthukumar.ai_crm.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Course create(Course course){
        return courseRepository.save(course);
    }

    public List<Course> getAll(){
        return courseRepository.findAll();
    }

    public Course getById(Long id){
        return courseRepository.findById(id).orElseThrow(()->new RuntimeException("Course not Found!"));
    }

    public Course getByCourseId(String courseId){
        return courseRepository.findByCourseId(courseId).orElseThrow(()->new RuntimeException("Course Not Found!"));
    }

    public List<Course> getByDomain(Long domainId){
        return courseRepository.findByDomain_Id(domainId).orElseThrow(()->new RuntimeException("Intern not found"));
    }

    public Page<Course> getByPages(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return courseRepository.findAll(pageable);
    }

    public Course update(Long id, Course course){
        Course existing = getById(id);
        existing.setName(course.getName());
        existing.setAmount(course.getAmount());
        existing.setDuration(course.getDuration());
        return courseRepository.save(existing);
    }

    public Course updateByCourseId(String courseId, Course course){
        Course existing = getByCourseId(courseId);
        existing.setName(course.getName());
        existing.setAmount(course.getAmount());
        existing.setDuration(course.getDuration());
        return courseRepository.save(existing);
    }

    public void deleteById(Long id){
        courseRepository.deleteById(id);
    }

}
