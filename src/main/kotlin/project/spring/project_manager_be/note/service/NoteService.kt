package project.spring.project_manager_be.note.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.spring.project_manager_be.note.entity.NoteEntity
import project.spring.project_manager_be.note.repository.NoteRepository
import project.spring.project_manager_be.note.NoteRequest
import project.spring.project_manager_be.note.NoteTagRequest
import project.spring.project_manager_be.utill.ByteUtil
import java.time.LocalDateTime

@Service
class NoteService(
    private val noteRepository: NoteRepository,
) {

    fun findProjectNoteList(projectId: Long) : List<NoteEntity> =
      noteRepository.findAllWithTagsByProjectIdOrdOrderByCreatedAt(projectId)

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
    fun updateNoteRaw(noteId : Long, raw : String) : NoteEntity{
        val oldNote = noteRepository.findById(noteId).orElseThrow {
            throw IllegalArgumentException("노트 아이디가 유효하지 않습니다: $noteId")
        }
        oldNote.raw = raw
        return noteRepository.save(oldNote)
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

    fun hasNote(noteId : Long){
        val isExistsNote = noteRepository.existsById(noteId)
        if (!isExistsNote){
            throw IllegalArgumentException("노트가 존재하지 않습니다. (ID: $noteId)")
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