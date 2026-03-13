package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.model.Intern;
import dev.muthukumar.ai_crm.repository.InternRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternService {

    private final InternRepository internRepository;

    public Intern create(Intern intern){
        return internRepository.save(intern);
    }

    public List<Intern> getAll(){
        return internRepository.findAll();
    }

    public Intern getById(Long id){
        return internRepository.findById(id).orElseThrow(()->new RuntimeException("Intern not Found"));
    }

    public List<Intern> getByDomain(Long domainId){
        return internRepository.findByDomain_Id(domainId).orElseThrow(()->new RuntimeException("Intern not found"));
    }

    public Page<Intern> getByPages(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return internRepository.findAll(pageable);
    }

    public Intern update(Intern intern){
        return internRepository.save(intern);
    }

    public Intern updateById(Long id, Intern intern){
        Intern existing = getById(id);
        existing.setTitle(intern.getTitle());
        existing.setDomain(intern.getDomain());
        existing.setDuration(intern.getDuration());
        existing.setAmount(intern.getAmount());
        return internRepository.save(existing);
    }

    public void deleteById(Long id){
        internRepository.deleteById(id);
    }

    public void delete(Intern intern){
        internRepository.delete(intern);
    }


}
