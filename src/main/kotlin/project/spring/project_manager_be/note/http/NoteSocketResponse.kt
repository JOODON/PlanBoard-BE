package project.spring.project_manager_be.note.http

import project.spring.project_manager_be.note.entity.NoteEntity

data class NoteSocketResponse(
    val type : String,

    val id: Long? = 0,

    val projectId : Long,

    var userId: Long,

    var raw: String,

    var isShareEdit : Boolean,

) {
    companion object {
        fun toResponse(noteEntity: NoteEntity, type: String) =
            NoteSocketResponse(
                type = type,
                id = noteEntity.id,
                projectId = noteEntity.projectId,
                userId = noteEntity.userId,
                raw = noteEntity.raw,
                isShareEdit = noteEntity.isShareEdit
            )

    }
}