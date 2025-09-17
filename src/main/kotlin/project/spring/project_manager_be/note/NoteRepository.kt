package project.spring.project_manager_be.note

import org.springframework.data.jpa.repository.JpaRepository

interface NoteRepository : JpaRepository<NoteEntity, Long> {

    fun findAllByProjectIdOrderByCreatedAt(projectId : Long) : List<NoteEntity>

}