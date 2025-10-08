package project.spring.project_manager_be.utill

import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtil {

    fun getUserId() = SecurityContextHolder.getContext()
        .authentication
        .principal as Long
}