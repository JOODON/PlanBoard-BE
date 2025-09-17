package project.spring.project_manager_be.project

data class ProjectUpdateRequest(
    val name : String?,
    val start : String?,
    val deadline : String?,
) {

}