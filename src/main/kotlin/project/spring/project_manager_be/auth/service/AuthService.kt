package project.spring.project_manager_be.auth.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import project.spring.project_manager_be.auth.entity.AuthEntity
import project.spring.project_manager_be.auth.http.AuthRequest
import project.spring.project_manager_be.auth.http.LoginRequest
import project.spring.project_manager_be.auth.http.LoginResponse
import project.spring.project_manager_be.auth.repository.AuthRepository
import project.spring.project_manager_be.config.jwt.JwtProvider
import project.spring.project_manager_be.user.UserService

private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

@Service
class AuthService (
    private val authRepository: AuthRepository,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider,
){

    fun createAuth(authRequest: AuthRequest): AuthEntity {
        val requestUser = authRequest.user
        val user = userService.findOrCreateUser(requestUser)
        val authEntity = AuthEntity.toEntity(authRequest, user.id!!)

        validateEmailForSignup(authEntity.email)
        validatePasswordForSignup(authEntity.password)
        authEntity.password = passwordEncoder.encode(authEntity.password)

        return authRepository.save(authEntity)
    }

    fun login(loginRequest: LoginRequest): LoginResponse {
        val authInfo = authRepository.findByEmail(loginRequest.email)
            ?: throw IllegalArgumentException("해당 유저는 존재하지 않습니다.")

        // 비밀번호 일치 여부 확인

        if (!passwordEncoder.matches(loginRequest.password, authInfo.password)) {
            throw IllegalArgumentException("비밀번호가 올바르지 않습니다.")
        }

        val accessToken = jwtProvider.createAccessToken(authInfo.userId, authInfo.email)
        val refreshToken = jwtProvider.createRefreshToken(authInfo.userId, authInfo.email)

        return LoginResponse(
            userId = authInfo.userId,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun validateEmailForSignup(email: String) {
        // 1. 이메일 형식 검증
        if (!EMAIL_REGEX.matches(email)) {
            throw IllegalArgumentException("올바르지 않은 이메일 형식입니다")
        }

        // 2. 이메일 중복 검증
        if (authRepository.existsByEmail(email)) {
            throw IllegalArgumentException("이미 사용 중인 이메일입니다")
        }
    }

    fun validatePasswordForSignup(password: String) {
        // 최소 8자 이상
        if (password.length < 8) {
            throw IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다")
        }

        // 영문 포함 여부
        if (!password.any { it.isLetter() }) {
            throw IllegalArgumentException("비밀번호는 영문자를 포함해야 합니다")
        }

        // 숫자 포함 여부
        if (!password.any { it.isDigit() }) {
            throw IllegalArgumentException("비밀번호는 숫자를 포함해야 합니다")
        }

        // 특수문자 포함 여부
        val specialChars = "!@#\$%^&*()_+-=[]{}|;:',.<>?/~`"

        if (!password.any { it in specialChars }) {
            throw IllegalArgumentException("비밀번호는 특수문자를 포함해야 합니다")
        }
    }



}