package dev.muthukumar.ai_crm.dto;

import dev.muthukumar.ai_crm.enums.AllocationCategory;
import java.time.LocalDate;
import java.time.LocalTime;

public class AllocationRequest {
    public Long studentId;
    public AllocationCategory category;

    // Catalog FK (only one needed based on category)
    public Long courseId;
    public Long internId;
    public Long projectId;

    // Responsible employee
    public Long assignedEmployeeId;

    // Timing (for COURSE / INTERN)
    public LocalDate startDate;
    public LocalDate endDate;
    public LocalTime startTime;
    public LocalTime endTime;

    public String notes;
}
