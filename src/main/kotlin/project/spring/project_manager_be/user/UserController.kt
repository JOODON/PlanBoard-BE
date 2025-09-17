package project.spring.project_manager_be.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import project.spring.project_manager_be.utill.ApiResponse

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun auth(@RequestBody user: UserEntity) =
        ApiResponse(code = 200, message = "유저 인증 성공", data = userService.findOrCreateUser(user))

    @GetMapping
    fun userInfo(@RequestHeader userId : Long) =
        ApiResponse(code = 200, message = "유저 정보 조회 성공", data =userService.findUserById(userId))

}