package project.spring.project_manager_be.config.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import project.spring.project_manager_be.utill.ApiResponse

@Component
class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper

) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json;charset=UTF-8"

        val errorResponse = ApiResponse(
            code = 401,
            message = authException.message ?: "인증이 필요합니다",
            data = null
        )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }


}