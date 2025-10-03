package project.spring.project_manager_be.auth.http

import project.spring.project_manager_be.user.UserEntity

data class AuthRequest(
    val user: UserEntity,
    val email : String,
    val password : String
) {

}