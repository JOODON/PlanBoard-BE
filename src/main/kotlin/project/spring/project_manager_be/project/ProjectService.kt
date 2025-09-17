package project.spring.project_manager_be.project

import org.springframework.stereotype.Service
import project.spring.project_manager_be.note.NoteService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val noteService: NoteService
){

    fun findProjectListByUserId(userId: Long) =
        projectRepository.findAllByUserIdOrderByCreatedAt(userId)

    fun createProject(userId: Long, projectEntity: ProjectEntity): ProjectEntity =
        projectEntity.apply {
            validationProject(projectEntity)

            this.userId = userId
            this.status = if (isDateTimeInPast(projectEntity.deadline)) "완료" else "진행중"

        }.let { projectRepository.save(it) }

    fun updateProject(userId: Long, projectId: Long, projectUpdateRequest: ProjectUpdateRequest): ProjectEntity {
        val oldProjectEntity = findProjectById(projectId)
        isOwner(userId, oldProjectEntity)
        val updateProjectData = updateProjectRequestToEntity(projectUpdateRequest, oldProjectEntity, projectId, userId)
        return projectRepository.save(updateProjectData)
    }

    fun deleteProject(userId : Long, projectId : Long) {
        val projectEntity = findProjectById(projectId)

        projectEntity.apply {
            isOwner(userId, projectEntity)
        }.let { projectRepository.delete(it) }

        noteService.deleteNoteByProjectId(userId, projectId)
    }

    fun findProjectById(projectId: Long): ProjectEntity =
        projectRepository.findById(projectId).orElseThrow {
        IllegalArgumentException("해당 프로젝트가 존재하지 않습니다. (ID: ${projectId})")
    }


    fun isOwner(userId: Long, projectEntity: ProjectEntity) {
        val project = projectRepository.findById(projectEntity.id!!).orElseThrow {
            IllegalArgumentException("해당 프로젝트가 존재하지 않습니다. (ID: ${projectEntity.id})")
        }

        if (project.userId != userId) {
            throw IllegalArgumentException("해당 프로젝트의 소유자가 아닙니다. (ID: ${project.id})")
        }
    }

    fun validationProject(projectEntity : ProjectEntity){
        if (!isValidDate(projectEntity.start)) {
            throw IllegalArgumentException("시작 날짜가 올바른 형식이 아닙니다: ${projectEntity.start}")
        }

        if (!isValidDate(projectEntity.deadline)) {
            throw IllegalArgumentException("마감 날짜가 올바른 형식이 아닙니다: ${projectEntity.deadline}")
        }

        if (!isDeadLineAfterStart(projectEntity.start, projectEntity.deadline)) {
            throw IllegalArgumentException("종료 날짜는 시작 날짜 이후여야 합니다: ${projectEntity.deadline}")
        }
    }

    fun isDateTimeInPast(dateStr: String, pattern: String = "yyyy-MM-dd"): Boolean {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val inputDate = LocalDate.parse(dateStr, formatter)
        return inputDate.isBefore(LocalDate.now())
    }

    fun isValidDate(dateTime: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            LocalDate.parse(dateTime, formatter)
            true // 정상적인 날짜
        } catch (e: DateTimeParseException) {
            false // 형식이 맞지 않거나 유효하지 않은 날짜
        }
    }
    fun isDeadLineAfterStart(start: String, deadLine: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val startDate = LocalDate.parse(start, formatter)
        val endDate = LocalDate.parse(deadLine, formatter)

        // 종료일이 시작일 이후면 true
        return endDate.isAfter(startDate)
    }

    fun updateProjectRequestToEntity(
        projectUpdateRequest: ProjectUpdateRequest,
        projectEntity: ProjectEntity,
        projectId: Long,
        userId: Long
    ) = ProjectEntity(
        id = projectId,
        userId = userId,
        name = projectUpdateRequest.name ?: projectEntity.name, // 요청 값이 null이면 기존 값 사용
        status = if (isDateTimeInPast(projectUpdateRequest.deadline ?: projectEntity.deadline)) "완료" else "진행중",
        start = projectUpdateRequest.start ?: projectEntity.start,
        deadline = projectUpdateRequest.deadline ?: projectEntity.deadline, // deadline도 동일 처리
    )



}