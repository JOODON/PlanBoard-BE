package project.spring.project_manager_be.note.dto

import project.spring.project_manager_be.note.entity.NoteEntity
import project.spring.project_manager_be.note.entity.NoteShareEntity
import project.spring.project_manager_be.note.entity.NoteTagEntity
import java.time.LocalDateTime

data class NoteDto(
    val id: Long? = 0,
    val projectId: Long,
    var userId: Long,
    val shareId : Long?,

    var raw: String,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    var rawBytes: Long = 0,
    var remainingBytes: Long = 0,
    var usedPercent: Int = 0,
    var remainingPercent: Int = 0,
    val tags: List<NoteTagEntity> = emptyList(),
    val connUrl: String?
) {
    companion object {

        fun toDto(noteEntity: NoteEntity, tags: List<NoteTagEntity>, noteShareEntity: NoteShareEntity?): NoteDto {
            return NoteDto(
                id = noteEntity.id,
                projectId = noteEntity.projectId,
                userId = noteEntity.userId,
                shareId = noteShareEntity?.id,
                raw = noteEntity.raw,
                createdAt = noteEntity.createdAt,
                updatedAt = noteEntity.updatedAt,
                rawBytes = noteEntity.rawBytes,
                remainingBytes = noteEntity.remainingBytes,
                usedPercent = noteEntity.usedPercent,
                remainingPercent = noteEntity.remainingPercent,
                tags = tags,
                connUrl = noteShareEntity?.shareUrl
            )
        }
    }
}
