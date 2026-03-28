package dev.muthukumar.anjana_crm.data.api

import dev.muthukumar.anjana_crm.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    // ── Students ──────────────────────────────────────────────
    @GET("students")
    suspend fun getStudents(): Response<List<Student>>

    @GET("students/{id}")
    suspend fun getStudentById(@Path("id") id: Long): Response<Student>

    // ── Allocations ───────────────────────────────────────────
    @GET("allocations")
    suspend fun getAllocations(): Response<ApiListResponse<Allocation>>

    @GET("allocations/student/{id}")
    suspend fun getAllocationsByStudent(@Path("id") id: Long): Response<ApiListResponse<Allocation>>

    @GET("allocations/employee/{id}")
    suspend fun getAllocationsByEmployee(@Path("id") id: Long): Response<ApiListResponse<Allocation>>

    @PATCH("allocations/{id}/status")
    suspend fun updateAllocationStatus(
        @Path("id") id: Long,
        @Body body: Map<String, String>
    ): Response<ApiResponse<Allocation>>

    @PATCH("allocations/{id}/assign")
    suspend fun updateAssignment(
        @Path("id") id: Long,
        @Body body: Map<String, Any>
    ): Response<ApiResponse<Allocation>>

    // ── Certificates ──────────────────────────────────────────
    @GET("certificates")
    suspend fun getCertificates(): Response<ApiListResponse<Certificate>>

    @POST("certificates")
    suspend fun issueCertificate(@Body body: Map<String, Any>): Response<ApiResponse<Certificate>>

    // ── Payments ──────────────────────────────────────────────
    @GET("payments")
    suspend fun getPayments(
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<ApiListResponse<Payment>>

    @GET("salary/employee/{id}")
    suspend fun getSalaryByEmployee(@Path("id") id: Long): Response<List<Salary>>

    @POST("salary")
    suspend fun createSalary(@Body body: SalaryRequest): Response<Salary>

    // ── Invoices ──────────────────────────────────────────────
    @GET("invoices")
    suspend fun getInvoices(): Response<ApiListResponse<Invoice>>

    @POST("invoices")
    suspend fun createInvoice(@Body body: Map<String, Any>): Response<ApiResponse<Invoice>>

    @PATCH("invoices/{id}/pay")
    suspend fun markInvoicePaid(@Path("id") id: Long): Response<ApiResponse<Invoice>>

    // ── Finance transactions ──────────────────────────────────
    @GET("finance/transactions")
    suspend fun getTransactions(): Response<ApiListResponse<FinanceTransaction>>

    @POST("finance/transactions")
    suspend fun createTransaction(@Body body: Map<String, Any>): Response<ApiResponse<FinanceTransaction>>

    @PATCH("finance/transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: Long,
        @Body body: Map<String, Any>
    ): Response<ApiResponse<FinanceTransaction>>

    @DELETE("finance/transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Long): Response<ApiResponse<Unit>>

    // ── Users ─────────────────────────────────────────────────
    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    // ── Tickets ───────────────────────────────────────────────
    @POST("tickets")
    suspend fun submitTicket(@Body body: TicketRequest): Response<ApiResponse<Ticket>>

    @GET("tickets")
    suspend fun getTickets(): Response<ApiListResponse<Ticket>>

    // Salary
    @GET("salary")
    suspend fun getAllSalaries(): Response<ApiResponse<List<Salary>>>

    @POST("salary")
    suspend fun createSalary(@Body body: Map<String, Any?>): Response<ApiResponse<Salary>>

    @PATCH("salary/{id}/pay")
    suspend fun markSalaryPaid(@Path("id") id: Long): Response<ApiResponse<Salary>>

    // Finance
    @GET("finance/transactions")
    suspend fun getAllTransactions(
        @Query("start") start: String? = null,
        @Query("end") end: String? = null
    ): Response<ApiResponse<List<Transaction>>>

    @GET("finance/summary")
    suspend fun getFinanceSummary(
        @Query("start") start: String? = null,
        @Query("end") end: String? = null
    ): Response<ApiResponse<FinanceSummary>>

    @POST("finance/transactions")
    suspend fun createTransaction(@Body body: Map<String, Any?>): Response<ApiResponse<Transaction>>

}