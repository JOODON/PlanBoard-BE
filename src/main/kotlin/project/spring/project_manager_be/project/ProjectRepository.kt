package project.spring.project_manager_be.project

import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepository : JpaRepository<ProjectEntity, Long> {

    fun findAllByUserIdOrderByCreatedAt(userId: Long): List<ProjectEntity>

}