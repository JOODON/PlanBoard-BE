package project.spring.project_manager_be.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.spring.project_manager_be.auth.entity.AuthEntity

interface AuthRepository : JpaRepository<AuthEntity, Long> {

    fun existsByEmail(email: String): Boolean

    fun existsByUserId(userId: Long): Boolean

    fun findByEmail(email: String): AuthEntity?
}