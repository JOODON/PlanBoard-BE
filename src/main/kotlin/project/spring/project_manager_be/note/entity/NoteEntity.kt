package project.spring.project_manager_be.note.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import project.spring.project_manager_be.note.http.NoteRequest
import project.spring.project_manager_be.utill.ByteUtil
import java.time.LocalDateTime
import kotlin.math.ceil

@Entity
@Table(name = "tb_note")
class NoteEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = 0,

    val projectId: Long,

    @Column(nullable = false)
    var userId: Long,

    @Column(name = "raw", columnDefinition = "TEXT")
    var raw: String,

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var rawBytes: Long = 0,

    @Column(nullable = false)
    var remainingBytes: Long = 0,

    @Column(nullable = false)
    var usedPercent: Int = 0,

    @Column(nullable = false)
    var remainingPercent: Int = 0,

    @Transient
    var isShareEdit: Boolean,

    ) {
    fun updateUsage(maxBytes: Long) {
        val bytes = raw.toByteArray(Charsets.UTF_8).size.toLong()
        rawBytes = bytes
        remainingBytes = maxBytes - bytes
        usedPercent = ceil(bytes * 100.0 / maxBytes).toInt()
        remainingPercent = 100 - usedPercent
    }

    @PrePersist
    @PreUpdate
    fun preSave() {
        updatedAt = LocalDateTime.now()
        updateUsage(ByteUtil.MAX_NOTE_SIZE)
    }

    companion object {
        fun toNoteEntity(userId: Long, noteRequest: NoteRequest) =
            NoteEntity(
                id = noteRequest.id,
                userId = userId,
                projectId = noteRequest.projectId,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                raw = noteRequest.raw,
                isShareEdit = true,
            )

        fun toUpdateNoteEntity(request: NoteRequest, oldNote: NoteEntity): NoteEntity =
            NoteEntity(
                id = oldNote.id,
                userId = oldNote.userId,
                projectId = oldNote.projectId,
                createdAt = oldNote.createdAt,
                updatedAt = LocalDateTime.now(),
                raw = request.raw,
                isShareEdit = true,
            )

    }
}
