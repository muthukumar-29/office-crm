package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.model.Allocation;
import dev.muthukumar.ai_crm.repository.AllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllocationService {

    private final AllocationRepository allocationRepository;

    public Allocation create(Allocation allocation){
        return allocationRepository.save(allocation);
    }

    public List<Allocation> getAll(){
        return allocationRepository.findAll();
    }

    public Page<Allocation> getByPages(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return allocationRepository.findAll(pageable);
    }

    public Allocation getById(Long id){
        return allocationRepository.findById(id).orElseThrow(()->new RuntimeException("Allocation not found"));
    }

    public Allocation update(Long id, Allocation allocation){
        Allocation existing = getById(id);
        existing.setCategory(allocation.getCategory());
        existing.setDomain(allocation.getDomain());
        existing.setReferenceId(allocation.getReferenceId());
        existing.setStudent(allocation.getStudent());
        existing.setAmount(allocation.getAmount());
        return allocationRepository.save(existing);
    }

    public void delete(Long id){
        allocationRepository.deleteById(id);
    }

}
