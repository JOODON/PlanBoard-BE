package project.spring.project_manager_be.note.dto

// DTO
data class NoteUsageDto(
    val id: Long,
    val raw: String?,
    val rawBytes: Long,
    val remainingBytes: Long,
    val usedPercent: Int,
    val remainingPercent: Int
)
