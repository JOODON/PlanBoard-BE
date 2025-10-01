package project.spring.project_manager_be.note.dto

data class NoteShareEditDto(
    val editTime: Long,
    val userId : Long,
    val raw : String
) {

}