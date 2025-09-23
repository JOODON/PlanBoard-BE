package project.spring.project_manager_be.note.dto

data class NoteShareDto(
    val requestUserId : Long,
    val ownerUserId : Long,
    val noteId : Long
) {

}