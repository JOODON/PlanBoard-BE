package project.spring.project_manager_be.project

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import project.spring.project_manager_be.utill.ApiResponse
import project.spring.project_manager_be.utill.SecurityUtil

@RestController
@RequestMapping("/api/projects")
class ProjectController(
    private val projectService: ProjectService
) {

    @GetMapping
    fun projectList() =
        ApiResponse(code = 200, message = "프로젝트 조회 성공", data = projectService.findProjectListByUserId(SecurityUtil.getUserId()))

    @PostMapping
    fun addProject(@RequestBody projectEntity: ProjectEntity) =
        ApiResponse(code = 200, message = "프로젝트 생성 성공", data = projectService.createProject(SecurityUtil.getUserId(), projectEntity))

    @PutMapping("/{projectId}")
    fun updateProject(
        @PathVariable projectId: Long,
        @RequestBody projectUpdateRequest: ProjectUpdateRequest) =
        ApiResponse(code = 200, message = "프로젝트 업데이트 성공", data = projectService.updateProject(SecurityUtil.getUserId(),projectId ,projectUpdateRequest))

    @DeleteMapping("/{projectId}")
    fun deleteProject(
        @RequestHeader userId: Long, @PathVariable projectId: Long) =
        ApiResponse(code = 200, message = "프로젝트 삭제 성공", data = projectService.deleteProject(userId, projectId))


}