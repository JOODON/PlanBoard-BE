package project.spring.project_manager_be.note.http

data class NoteShareRequest(
    val noteId : Long,
    val projectId : Long
) {
}