package project.spring.project_manager_be.note.dto

data class NoteShareSocketDto(
    val requestUserId : Long,
    val ownerUserId : Long,
    val noteId : Long,
    val projectId : Long
) {

}