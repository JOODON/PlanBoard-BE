package project.spring.project_manager_be.note

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.spring.project_manager_be.utill.APIUtil
import project.spring.project_manager_be.utill.ByteUtil
import project.spring.project_manager_be.utill.CryptoUtil
import java.time.LocalDateTime

@Service
class NoteService(
    private val noteRepository: NoteRepository,
) {

    fun findProjectNoteList(projectId: Long) =
       noteRepository.findAllByProjectIdOrderByCreatedAt(projectId)

    @Transactional
    fun createNode(userId : Long, noteRequest: NoteRequest){
        val saveNoteEntity = toNoteEntity(userId, noteRequest)
        saveNoteEntity.userId = userId

        validateNoteSize(saveNoteEntity)
        noteRepository.save(saveNoteEntity)
    }

    @Transactional
    fun updateNote(userId: Long, noteId: Long, noteRequest: NoteRequest) {

        val oldNote = noteRepository.findById(noteId).orElseThrow {
            throw IllegalArgumentException("노트 아이디가 유효하지 않습니다: $noteId")
        }

        noteRequest.id = noteId
        val updateNote = toNoteEntity(userId , noteRequest)
        updateNote.createdAt = oldNote.createdAt
        //노트 사이즈 체크
        validateNoteSize(updateNote)

        noteRepository.save(updateNote)
    }

    @Transactional
    fun deleteNoteByProjectId(userId: Long, projectId: Long) {
        val noteList = findProjectNoteList(projectId)

        noteList.map { deleteNoteEntity ->
            deleteNoteById(userId, deleteNoteEntity.id!!)
        }
    }

    @Transactional
    fun deleteNoteById(userId: Long, noteId: Long){
        val deleteNoteEntity = noteRepository.findById(noteId).orElseThrow {
                throw IllegalArgumentException("노트 아이디가 유효하지 않습니다: $noteId")
            }

        if (deleteNoteEntity.userId != userId){
            throw IllegalArgumentException("해당 노트를 삭제할 권한이 없습니다 .$noteId")
        }

        noteRepository.delete(deleteNoteEntity)
    }

    @Transactional
    fun createShareNoteUrl(userId: Long, noteId: Long): Map<String, String> {
        val isExistsNote = noteRepository.existsById(noteId)
        if (!isExistsNote){
            throw IllegalArgumentException("노트가 존재하지 않습니다. (ID: $noteId)")
        }
        //TODO 소켓 연결 되는거 확인 후 여기서 DB 저장이 추가되어야할듯!
        val payload = "${userId}_x_${noteId}"              // 구분자 _x_ 사용
        val encrypted = CryptoUtil.encrypt(payload)       // 암호화
        val baseApi = APIUtil.SERVER_BASE_API
            .removePrefix("http://")
            .removePrefix("https://")
            .removeSuffix("/")  // 맨 끝 슬래시 제거

        val wsUrl = "ws://$baseApi/project-manager/ws/note?code=$encrypted"

        return mapOf("url" to wsUrl)
    }

    fun getNoteById(noteId: Long): NoteEntity =
        noteRepository.findById(noteId)
            .orElseThrow { IllegalArgumentException("노트가 존재하지 않습니다. (ID: $noteId)") }

    fun validateNoteSize(noteEntity: NoteEntity){
        val noteContent = noteEntity.raw

        val contentSize = noteContent.toByteArray().size
        if (contentSize > ByteUtil.MAX_NOTE_SIZE) {
            throw IllegalArgumentException("문서 용량이 너무 큽니다. 5MB 이하로 제한하세요.")
        }
    }

    fun toNoteEntity(userId: Long, noteRequest: NoteRequest) =
        NoteEntity(
            id = noteRequest.id,
            userId = userId,
            projectId = noteRequest.projectId,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            raw = noteRequest.raw,
            isShareEdit = true
        )

}