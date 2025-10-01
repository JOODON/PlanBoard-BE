package project.spring.project_manager_be.note.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import project.spring.project_manager_be.note.entity.NoteEntity
import project.spring.project_manager_be.note.entity.NoteShareEntity
import project.spring.project_manager_be.note.entity.NoteTagEntity

interface NoteTagRepository : JpaRepository<NoteTagEntity, Long> {

    @Modifying
    @Query("DELETE FROM NoteTagEntity tnt WHERE tnt.noteId = :noteId")
    fun deleteAllByNote(@Param("noteId") note: Long) : Int

    fun findByNoteIdIn(ids : List<Long>) : List<NoteTagEntity>
}