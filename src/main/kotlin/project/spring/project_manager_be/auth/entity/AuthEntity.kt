package project.spring.project_manager_be.auth.entity

import jakarta.persistence.*
import project.spring.project_manager_be.auth.http.AuthRequest
import java.time.LocalDateTime

@Entity
@Table(name = "tb_auth")
class AuthEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)  // unique 추가!
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false, updatable = false)
    val userId: Long, //기존 유저 정보를 담고있는 테이블 킼

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true, updatable = false)
    val lastLoginAt: LocalDateTime? = null

) {
    companion object {
        fun toEntity(authRequest: AuthRequest, userId: Long): AuthEntity =
            AuthEntity(
                id = null,
                email = authRequest.email,
                password = authRequest.password,
                userId = userId,
                createdAt = LocalDateTime.now(),
                lastLoginAt = null
            )
    }
}

