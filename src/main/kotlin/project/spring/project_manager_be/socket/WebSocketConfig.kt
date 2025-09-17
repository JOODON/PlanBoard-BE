package project.spring.project_manager_be.socket

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import project.spring.project_manager_be.socket.handler.NoteWebSocketHandler

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val noteWebSocketHandler: NoteWebSocketHandler  // DI로 주입받기
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(noteWebSocketHandler, "/ws/note")
            .setAllowedOrigins("*")
    }
}

