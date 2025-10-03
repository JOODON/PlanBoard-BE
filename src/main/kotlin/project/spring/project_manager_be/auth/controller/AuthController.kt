package project.spring.project_manager_be.auth.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import project.spring.project_manager_be.auth.http.AuthRequest
import project.spring.project_manager_be.auth.service.AuthService
import project.spring.project_manager_be.utill.ApiResponse

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping
    fun addAuth(@RequestBody authRequest: AuthRequest) =
        ApiResponse(code = 200, "회원가입 성공", authService.createAuth(authRequest))


}