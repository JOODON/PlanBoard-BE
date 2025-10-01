package project.spring.project_manager_be.note.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.spring.project_manager_be.note.dto.NoteDto
import project.spring.project_manager_be.note.entity.NoteEntity
import project.spring.project_manager_be.note.repository.NoteRepository
import project.spring.project_manager_be.note.http.NoteRequest
import project.spring.project_manager_be.note.repository.NoteShareRepository
import project.spring.project_manager_be.note.repository.NoteTagRepository
import project.spring.project_manager_be.utill.ByteUtil

@Service
class NoteService(
    private val noteTagRepository: NoteTagRepository,
    private val noteRepository: NoteRepository,
    private val noteShareRepository: NoteShareRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun findProjectNoteList(projectId: Long): List<NoteDto> {
        val noteList = noteRepository.findAllByProjectIdOrderByCreatedAt(projectId)
        val noteIds = noteList.mapNotNull { it.id }  // null-safe

        if (noteIds.isEmpty()) return emptyList() // 안전 처리

        val tagList = noteTagRepository.findByNoteIdIn(noteIds)
        val noteTagsGroup = tagList.groupBy { it.noteId }

        return noteList.map { note ->
            NoteDto.toDto(
                note,
                noteTagsGroup[note.id] ?: emptyList(), // 빈 리스트 fallback
                null //반환시에는 NULL 로 처리하시는걸로 진행
            )
        }
    }

    @Transactional
    fun createNode(userId : Long, noteRequest: NoteRequest){
        val saveNoteEntity = NoteEntity.toNoteEntity(userId, noteRequest)
        validateNoteSize(saveNoteEntity)
        noteRepository.save(saveNoteEntity)
    }

    @Transactional
    fun updateNote(userId: Long, noteId: Long, noteRequest: NoteRequest) {
        val note = noteRepository.findById(noteId).orElseThrow {
            IllegalArgumentException("노트 아이디가 유효하지 않습니다: $noteId")
        }
        val updateNote = NoteEntity.toUpdateNoteEntity(noteRequest, note)
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

        val deleteNoteCnt = noteRepository.delete(deleteNoteEntity) //노트 원본 삭제
        val deleteTagCnt = noteTagRepository.deleteAllByNote(noteId) //노트 태그 삭제
        val deleteShareNoteCnt = noteShareRepository.deleteAllByNote(noteId) //공유된 노트 내역 삭제
        logger.info("삭제 결과 -> 원본: {}, 태그: {}, 공유노트: {}", deleteNoteCnt, deleteTagCnt, deleteShareNoteCnt)
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


}