package project.spring.project_manager_be.note.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import project.spring.project_manager_be.note.entity.NoteShareEntity

interface NoteShareRepository : JpaRepository<NoteShareEntity, Long> {

    fun findByUserIdAndProjectId(userId: Long, projectId: Long): List<NoteShareEntity>

    fun findByUserIdAndNoteIdAndProjectId(userId: Long, noteId: Long, projectId: Long): NoteShareEntity?

    @Modifying
    @Query("DELETE FROM NoteShareEntity tse WHERE tse.noteId = :noteId")
    fun deleteAllByNote(noteId: Long): Int
}