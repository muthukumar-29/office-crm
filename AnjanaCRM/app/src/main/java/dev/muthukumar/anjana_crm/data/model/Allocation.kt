package dev.muthukumar.anjana_crm.data.model

data class Allocation(
    val id: Long,
    val category: String,
    val allocationStatus: String,
    val paymentStatus: String,
    val projectStatus: String?,
    val internStatus: String?,
    val courseStatus: String?,
    val totalFee: Double?,
    val amountPaid: Double?,
    val balanceDue: Double?,
    val startDate: String?,
    val endDate: String?,
    val classStartTime: String?,
    val classEndTime: String?,
    val student: Student?,
    val assignedEmployee: User?,
    val course: CourseBrief?,
    val intern: InternBrief?,
    val project: ProjectBrief?,
    val notes: String?
)

data class CourseBrief(val id: Long, val name: String, val duration: String?, val amount: Int)
data class InternBrief(val id: Long, val title: String, val duration: String?, val amount: Int)
data class ProjectBrief(val id: Long, val title: String, val amount: Int)
