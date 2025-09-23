package project.spring.project_manager_be.socket.handler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import project.spring.project_manager_be.note.NoteSocketResponse
import project.spring.project_manager_be.note.dto.NoteShareDto
import project.spring.project_manager_be.note.service.NoteService
import project.spring.project_manager_be.note.service.NoteShareService
import project.spring.project_manager_be.user.UserService
import project.spring.project_manager_be.utill.CryptoUtil
import project.spring.project_manager_be.utill.JsonUtil

@Component
class NoteWebSocketHandler(
    private val noteService: NoteService,
    private val userService: UserService
) : TextWebSocketHandler() {
    //노트 ID 별로 세션을 처리
    private val noteSessions = mutableMapOf<Long, MutableSet<WebSocketSession>>()

    // 세션별 사용자 ID 매핑 (새로 추가)
    private val sessionUserMap = mutableMapOf<WebSocketSession, Map<String,String>>()

    //연결시 이벤트 처리
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val sessionInfo = getUserAndNoteInfoFromSession(session)
        val requestUserId = sessionInfo.requestUserId
        val ownerUserId = sessionInfo.ownerUserId
        val noteId = sessionInfo.noteId

        val sessions = noteSessions.getOrPut(noteId) { mutableSetOf() }
        sessions.add(session)

        // 세션-사용자 매핑 저장 (새로 추가)
        val userInfo = userService.findUserById(userId = requestUserId)
        sessionUserMap[session] = mapOf(
            "userId" to userInfo.id.toString(),
            "username" to userInfo.name
        )

        //노트 데이터 반환
        val noteData = noteService.getNoteById(noteId)
        noteData.isShareEdit = ownerUserId != requestUserId

        // 현재 참가자 목록 생성 (새로 추가)
        val participants = getParticipants(noteId)
        val noteResponseData = NoteSocketResponse.toResponse(noteData, "is-open")

        // ObjectMapper를 사용해서 직접 Map으로 변환
        val objectMapper = ObjectMapper()
        val responseMap = objectMapper.convertValue(noteResponseData, Map::class.java) as MutableMap<String, Any?>
        responseMap["participants"] = participants

        session.sendMessage(TextMessage(JsonUtil.toString(responseMap)))

        // 다른 참가자들에게 참가자 목록 업데이트 알림 (새로 추가)
        broadcastParticipantsUpdate(noteId)
    }

    //메세지 입력시 이벤트 처리
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val noteShareDto = getUserAndNoteInfoFromSession(session)
        val noteId = noteShareDto.noteId
        val requestUserId = noteShareDto.requestUserId

        val payload = message.payload
        val jsonNode = ObjectMapper().readTree(payload)
        println("연결중인 유저 ID $noteId , 데이터 형태 $payload")
        when (val type = jsonNode["type"].asText()) {
            "cursor-update" -> {
                println("커서 실시간 데이터 전송")
                handleCursorUpdate(noteId, session, jsonNode)
            }
            "update-note" -> {
                handleContentUpdate(noteId, requestUserId, jsonNode)
            }

            else -> println("알 수 없는 메시지 타입: $type")
        }
    }

    //종료시 이벤트 처리
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val noteId = getUserAndNoteInfoFromSession(session).noteId

        // 세션 제거
        noteSessions[noteId]?.remove(session)
        sessionUserMap.remove(session) // 사용자 매핑도 제거 (새로 추가)

        if (noteSessions[noteId]?.isEmpty() == true) {
            noteSessions.remove(noteId)
        }

        // 남은 참가자들에게 업데이트된 참가자 목록 전송 (새로 추가)
        if (noteSessions[noteId]?.isNotEmpty() == true) {
            broadcastParticipantsUpdate(noteId)
        }
    }

    private fun getUserAndNoteInfoFromSession(session: WebSocketSession): NoteShareDto {
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

        return NoteShareDto(
            requestUserId = requestUserId,
            ownerUserId = ownerUserId,
            noteId = noteId
        )
    }

    private fun handleCursorUpdate(noteId: Long, sender: WebSocketSession, jsonNode: JsonNode) {
        // 커서 위치 처리
        noteSessions[noteId]?.forEach {
            if (it != sender && it.isOpen)
                it.sendMessage(TextMessage(jsonNode.toString()))
        }
    }

    private fun handleContentUpdate(noteId: Long, requestUserId: Long, jsonNode: JsonNode) {
        // 노트 내용 처리
        val raw = jsonNode["raw"].asText()
        val updatedNote = noteService.updateNoteRaw(noteId, raw) //노트 내용을 업데이트

        val updatedMessage = mapOf(
            "type" to "update-note",
            "noteId" to updatedNote.id,
            "userId" to requestUserId,
            "raw" to updatedNote.raw
        )

        noteSessions[noteId]?.forEach { session ->
            if (session.isOpen) {
                try {
                    session.sendMessage(TextMessage(JsonUtil.toString(updatedMessage)))
                } catch (e: Exception) {
                    println("참가자 업데이트 메시지 전송 실패: ${e.message}")
                }
            }
        }
    }

    // 현재 참가자 목록 반환 (새로 추가)
    private fun getParticipants(noteId: Long): List<Map<String,String>> {
        return noteSessions[noteId]?.mapNotNull { session ->
            sessionUserMap[session]
        } ?: emptyList()
    }

    // 모든 참가자에게 참가자 목록 업데이트 전송 (새로 추가)
    private fun broadcastParticipantsUpdate(noteId: Long) {
        val participants = getParticipants(noteId)

        val updateMessage = mapOf(
            "type" to "participants-update",
            "participants" to participants
        )

        noteSessions[noteId]?.forEach { session ->
            if (session.isOpen) {
                try {
                    session.sendMessage(TextMessage(JsonUtil.toString(updateMessage)))
                } catch (e: Exception) {
                    println("참가자 업데이트 메시지 전송 실패: ${e.message}")
                }
            }
        }
    }
}