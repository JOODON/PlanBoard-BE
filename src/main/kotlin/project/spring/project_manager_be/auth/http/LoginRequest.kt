package project.spring.project_manager_be.auth.http

data class LoginRequest(
    val email: String,
    val password: String
)
