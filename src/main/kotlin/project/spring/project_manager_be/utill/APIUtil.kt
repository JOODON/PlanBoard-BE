package project.spring.project_manager_be.utill

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
object APIUtil {

    @Value("\${server-base-url}") // 5MB 기본값
    var SERVER_BASE_API: String = "http://localhost:18080/"

}