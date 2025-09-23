package project.spring.project_manager_be.note.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import project.spring.project_manager_be.note.entity.NoteEntity

interface NoteRepository : JpaRepository<NoteEntity, Long> {

    @Query("""
        SELECT n FROM NoteEntity n
        LEFT JOIN FETCH n.tags
        WHERE n.projectId = :projectId
    """)
    fun findAllWithTagsByProjectIdOrdOrderByCreatedAt(@Param("projectId") projectId: Long): List<NoteEntity>


    fun findAllByProjectIdOrderByCreatedAt(projectId : Long) : List<NoteEntity>

}