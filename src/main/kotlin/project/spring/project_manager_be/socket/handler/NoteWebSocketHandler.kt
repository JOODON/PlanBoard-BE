package project.spring.project_manager_be.socket.handler

import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import project.spring.project_manager_be.note.NoteService
import project.spring.project_manager_be.utill.CryptoUtil
import project.spring.project_manager_be.utill.JsonUtil

@Component
class NoteWebSocketHandler(
    private val noteService: NoteService
) : TextWebSocketHandler() {
    //노트 ID 별로 세션을 처리
    private val noteSessions = mutableMapOf<Long, MutableSet<WebSocketSession>>()

    //연결시 이벤트 처리
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val sessionInfo = getUserAndNoteInfoFromSession(session)
        val requestUserId = sessionInfo.first
        val ownerUserId = sessionInfo.second
        val noteId = sessionInfo.third

        val sessions = noteSessions.getOrPut(noteId) { mutableSetOf() }
        sessions.add(session)

        val noteData = noteService.getNoteById(noteId)
        noteData.isShareEdit = ownerUserId != requestUserId           //노트와 유저가 같지 않다면 Share Edit 를 True 로 설정

        session.sendMessage(TextMessage(JsonUtil.toString(noteData))) //반환객체는 TextMessage 객체 안에 감싸기
    }

    //메세지 입력시 이벤트 처리
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val noteId = getUserAndNoteInfoFromSession(session).third
        noteSessions[noteId]?.forEach {
            if (it != session && it.isOpen) it.sendMessage(message)
        }
    }
    //종료시 이벤트 처리
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val noteId = getUserAndNoteInfoFromSession(session).third
        noteSessions[noteId]?.remove(session)
        if (noteSessions[noteId]?.isEmpty() == true) {
            noteSessions.remove(noteId)
        }
    }

    private fun getUserAndNoteInfoFromSession(session: WebSocketSession): Triple<Long /*requestUserId*/, Long /*ownerId*/, Long /*noteId*/> {
        val encryptedCode = session.uri?.query?.split("&")?.find { it.startsWith("code=") }?.split("=")?.getOrNull(1)
            ?: throw IllegalArgumentException("유효하지 않은 링크입니다.")

        val requestUserId = session.uri?.query?.split("&")?.find { it.startsWith("requestUserId=") }
            ?.split("=")?.getOrNull(1)?.toLongOrNull()
            ?: throw IllegalArgumentException("접속 유저 정보가 없습니다.")

        val decrypted = CryptoUtil.decrypt(encryptedCode) // "userId_x_noteId"
        val parts = decrypted.split("_x_")
        if (parts.size != 2) throw IllegalArgumentException("유효하지 않은 링크입니다.")

        val ownerUserId = parts[0].toLongOrNull() ?: throw IllegalArgumentException("유효하지 않은 링크입니다.")
        val noteId = parts[1].toLongOrNull() ?: throw IllegalArgumentException("유효하지 않은 링크입니다.")

        return Triple(requestUserId, ownerUserId, noteId)
    }



}