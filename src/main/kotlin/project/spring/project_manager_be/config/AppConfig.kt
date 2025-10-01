package project.spring.project_manager_be.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppConfig(
    @Value("\${spring.application.name}") val appName: String,
    @Value("\${spring.profiles.active}") val activeProfile: String,
    @Value("\${server-base-url}") val serverBaseUrl: String,
    @Value("\${note-max-size}") val noteMaxSize: Long
)