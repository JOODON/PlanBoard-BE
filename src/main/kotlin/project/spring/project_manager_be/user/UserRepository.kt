package project.spring.project_manager_be.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository  : JpaRepository<UserEntity, Long>{

    fun findUserByNameAndPhoneAndBirth(name: String, phone: String, birth: String) : UserEntity?

}