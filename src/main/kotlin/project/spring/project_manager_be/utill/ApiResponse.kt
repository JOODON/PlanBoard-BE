package project.spring.project_manager_be.utill

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null
)
