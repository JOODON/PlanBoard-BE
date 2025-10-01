package project.spring.project_manager_be.note.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.spring.project_manager_be.note.entity.NoteEntity

interface NoteRepository : JpaRepository<NoteEntity, Long> {

    fun findAllByProjectIdOrderByCreatedAt(projectId : Long) : List<NoteEntity>

    fun findByIdIn(ids : List<Long>) : List<NoteEntity>
}