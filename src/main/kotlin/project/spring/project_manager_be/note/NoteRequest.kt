package project.spring.project_manager_be.note


data class NoteRequest(
    var id: Long?,
    val projectId: Long,
    val raw: String
)