package project.spring.project_manager_be.note.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.spring.project_manager_be.note.entity.NoteShareEntity
import project.spring.project_manager_be.note.repository.NoteShareRepository
import project.spring.project_manager_be.user.UserService
import project.spring.project_manager_be.utill.APIUtil
import project.spring.project_manager_be.utill.CryptoUtil

@Service
class NoteShareService(
    private val userService: UserService,
    private val noteService: NoteService,
    private val noteShareRepository: NoteShareRepository
) {

    @Transactional
    fun getNoteSocketUrl(userId: Long, noteId: Long): Map<String, String> {
        noteService.hasNote(noteId)
        return mapOf("url" to getOrCreateNoteShareUrl(userId, noteId))
    }

    @Transactional
    fun getOrCreateNoteShareUrl(userId: Long, noteId: Long): String {

        return noteShareRepository.findByUserIdAndNoteId(userId, noteId)?.shareUrl
            ?: run {
                val connUser = userService.findUserById(userId)
                val wsUrl = createNoteShareUrl(userId, noteId)
                saveNoteShareUrl(toNoteShareEntity(connUser.id!!, noteId, connUser.name, wsUrl)).shareUrl
            }
    }

    fun createNoteShareUrl(userId: Long, noteId: Long): String {
        val payload = "${userId}_x_${noteId}"              // 구분자 _x_ 사용
        val encrypted = CryptoUtil.encrypt(payload)       // 암호화
        val baseApi = APIUtil.SERVER_BASE_API
            .removePrefix("http://")
            .removePrefix("https://")
            .removeSuffix("/")  // 맨 끝 슬래시 제거

        return "ws://$baseApi/project-manager/ws/note?code=$encrypted"
    }

    fun saveNoteShareUrl(noteShareEntity: NoteShareEntity) =
        noteShareRepository.save(noteShareEntity)

    fun toNoteShareEntity(userId: Long, noteId: Long, username: String, shareUrl: String) =
        NoteShareEntity(
            id = null,
            noteId = noteId,
            userId = userId,
            username = username,
            shareUrl = shareUrl
        )
}