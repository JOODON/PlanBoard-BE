package project.spring.project_manager_be.note.http


data class NoteRequest(
    var id: Long?,
    val projectId: Long,
    val raw: String
)