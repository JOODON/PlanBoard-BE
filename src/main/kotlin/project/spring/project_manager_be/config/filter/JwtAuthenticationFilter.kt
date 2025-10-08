package project.spring.project_manager_be.config.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import project.spring.project_manager_be.config.jwt.JwtProvider

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = extractJwtFromRequest(request)

            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                val userId = jwtTokenProvider.getUserId(jwt)
                //여기에 email 추가 가능

                val authentication = UsernamePasswordAuthenticationToken(
                    userId,         // principal
                    null, // credentials
                    emptyList()     // authorities
                )

                //HTTP 요청의 추가 정보를 인증 객체에 첨부
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                //스프링 Context 전역에 값을 설정
                SecurityContextHolder.getContext().authentication = authentication

            }
        }catch (e : Exception){

        }
        filterChain.doFilter(request, response)
    }

    private fun extractJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

}