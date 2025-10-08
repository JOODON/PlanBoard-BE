package project.spring.project_manager_be.auth.http

data class LoginResponse(
    val userId : Long,
    val accessToken: String,
    val refreshToken : String
){
}