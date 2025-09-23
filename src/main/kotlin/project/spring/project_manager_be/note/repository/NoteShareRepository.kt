package project.spring.project_manager_be.note.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.spring.project_manager_be.note.entity.NoteShareEntity

interface NoteShareRepository : JpaRepository<NoteShareEntity, Long> {

    fun findByUserIdAndNoteId(userId: Long, noteId: Long) : NoteShareEntity?

}