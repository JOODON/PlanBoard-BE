package project.spring.project_manager_be.note.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.spring.project_manager_be.note.http.NoteTagRequest
import project.spring.project_manager_be.note.entity.NoteEntity
import project.spring.project_manager_be.note.entity.NoteTagEntity
import project.spring.project_manager_be.note.repository.NoteTagRepository

@Service
class NoteTagService(
    private val noteService: NoteService,
    private val noteTagRepository: NoteTagRepository
) {

    @Transactional
    fun updateTags(userId: Long, noteId: Long, noteTagRequest: NoteTagRequest): MutableList<NoteTagEntity> {
        val noteTagList = noteTagRequest.tags

        // 태그 개수 제한
        if (noteTagList.size > 5) {
            throw IllegalArgumentException("태그는 최대 5개 까지 생성 가능합니다. ${noteTagRequest.tags}")
        }

        // 태그 길이 제한 (10자 이상이면 예외 발생)
        if (noteTagList.any { it.length > 10 }) {
            throw IllegalArgumentException("태그는 문자의 크기는 최대 10자 까지 가능합니다. ${noteTagRequest.tags}")
        }

        val noteEntity = noteService.getNoteById(noteId)

        // 기존 태그 삭제
        noteTagRepository.deleteAllByNote(noteId)
        // 새로운 태그 저장
        val tagEntities = noteTagList.map { toTagEntity(it, noteEntity) }
        return noteTagRepository.saveAll(tagEntities)
    }


    fun toTagEntity(tag : String, noteEntity: NoteEntity) =
        NoteTagEntity(
            id = null,
            tag = tag,
            noteId = noteEntity.id!!
        )

}