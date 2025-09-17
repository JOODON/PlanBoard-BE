package project.spring.project_manager_be.utill

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


object JsonUtil {
    private val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(Jdk8Module())
        registerModule(JavaTimeModule())   // LocalDateTime 지원용
    }

    fun toString(any: Any): String {
        return objectMapper.writeValueAsString(any)
    }

}