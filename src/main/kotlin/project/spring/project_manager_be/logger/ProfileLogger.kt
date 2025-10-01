package project.spring.project_manager_be.logger

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import project.spring.project_manager_be.config.AppConfig

@Component
class ProfileLogger(
    private val appConfig: AppConfig
) {
    private val log: Logger = LoggerFactory.getLogger(ProfileLogger::class.java)

    @PostConstruct
    fun logActiveProfiles() {
        log.info("===== APP CONFIG =====")
        log.info("Active Profile : ${appConfig.activeProfile}")
        log.info("Application Name : ${appConfig.appName}")
        log.info("Server Base URL : ${appConfig.serverBaseUrl}")
        log.info("Note Max Size : ${appConfig.noteMaxSize}")
        log.info("=====================")
    }

}