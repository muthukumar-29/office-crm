package dev.muthukumar.anjana_crm.data.model

data class Ticket(
    val id: Long,
    val subject: String,
    val message: String,
    val status: String,
    val createdAt: String,
    val response: String?
)

data class TicketRequest(
    val subject: String,
    val message: String
)
