package project.spring.project_manager_be.user.http

import project.spring.project_manager_be.user.UserEntity


data class UserResponse(
    val id: Long? = null,
    var name: String,
    var phone: String,
    var birth: String,
    val isAuthExist: Boolean
) {
    companion object {
        fun toResponse(userEntity: UserEntity, isAuth: Boolean): UserResponse {
            return UserResponse(
                id = userEntity.id,
                name = userEntity.name,
                phone = userEntity.phone,
                birth = userEntity.birth,
                isAuthExist = isAuth
            )
        }
    }
}
