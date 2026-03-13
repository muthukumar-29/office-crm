package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.model.Domain;
import dev.muthukumar.ai_crm.repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DomainService {

    private final DomainRepository domainRepository;

    public List<Domain> getAll() {
        return domainRepository.findAll();
    }

    public Domain getById(Long id) {
        return domainRepository.findById(id).orElseThrow(() -> new RuntimeException("Domain Not Found"));
    }

    public Domain create(Domain domain){
        return domainRepository.save(domain);
    }

    public Domain update(Long id, Domain updated){
        Domain existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        return domainRepository.save(existing);
    }

    public void delete(Long id){
        domainRepository.deleteById(id);
    }

}
