package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.model.Domain;
import dev.muthukumar.ai_crm.model.Intern;
import dev.muthukumar.ai_crm.model.Project;
import dev.muthukumar.ai_crm.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public Project create(Project Project){
        return projectRepository.save(Project);
    }

    public List<Project> getAll(){
        return projectRepository.findAll();
    }

    public Project getById(Long id){
        return projectRepository.findById(id).orElseThrow(()->new RuntimeException("Project not Found"));
    }

    public List<Project> getByDomain(Long domainId){
        return projectRepository.findByDomain_Id(domainId).orElseThrow(()->new RuntimeException("Project not found"));
    }

    public Page<Project> getByPages(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return projectRepository.findAll(pageable);
    }

    public Project update(Project Project){
        return projectRepository.save(Project);
    }

    public Project updateById(Long id, Project Project){
        Project existing = getById(id);
        existing.setTitle(Project.getTitle());
        existing.setDomain(Project.getDomain());
        existing.setAmount(Project.getAmount());
        return projectRepository.save(existing);
    }

    public void deleteById(Long id){
        projectRepository.deleteById(id);
    }

    public void delete(Project Project){
        projectRepository.delete(Project);
    }


}
