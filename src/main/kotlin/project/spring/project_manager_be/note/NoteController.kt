package project.spring.project_manager_be.note

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import project.spring.project_manager_be.note.http.NoteRequest
import project.spring.project_manager_be.note.http.NoteShareRequest
import project.spring.project_manager_be.note.http.NoteTagRequest
import project.spring.project_manager_be.note.service.NoteService
import project.spring.project_manager_be.note.service.NoteShareService
import project.spring.project_manager_be.note.service.NoteTagService
import project.spring.project_manager_be.utill.ApiResponse
import project.spring.project_manager_be.utill.SecurityUtil

@RestController
@RequestMapping("/api/notes")
class NoteController(
    private val nodeService : NoteService,
    private val noteShareService: NoteShareService,
    private val noteTagService: NoteTagService
) {
    //노트 List
    @GetMapping("/{projectId}")
    fun noteList(@PathVariable projectId : Long) =
        ApiResponse(code = 200, message = "노트 목록 조회 성공", data = nodeService.findProjectNoteList(projectId))

    @PostMapping
    fun addNote(@RequestBody noteRequest: NoteRequest) =
        ApiResponse(code = 200, message = "프로젝트 조회 성공", data = nodeService.createNode(SecurityUtil.getUserId() , noteRequest))

    @PutMapping("/{noteId}")
    fun updateNote(@PathVariable noteId: Long, @RequestBody noteRequest: NoteRequest) =
        ApiResponse(code = 200, message = "프로젝트 업데이트 성공", data = nodeService.updateNote(SecurityUtil.getUserId(), noteId, noteRequest))

    @PutMapping("/{noteId}/tags")
    fun updateNoteTag(
        @PathVariable noteId: Long,
        @RequestBody noteTagRequest: NoteTagRequest
    ) = ApiResponse(
        code = 200,
        message = "노트 태그 업데이트 성공",
        data = noteTagService.updateTags(SecurityUtil.getUserId(), noteId, noteTagRequest)
    )

    @DeleteMapping("/{noteId}")
    fun deleteNote(@PathVariable noteId: Long) =
        ApiResponse(code = 200, message = "프로젝트 삭제 성공", data = nodeService.deleteNoteById(SecurityUtil.getUserId(), noteId))


    @PostMapping("/share")
    fun addShareNote(@RequestBody noteShareRequest: NoteShareRequest) =
        ApiResponse(
            code = 200,
            message = "노트 공유 링크 생성 성공",
            data = noteShareService.createSharedNote(SecurityUtil.getUserId(), noteShareRequest)
        )

    @GetMapping("/share/{projectId}")
    fun shareNoteList(@PathVariable projectId: Long) =
        ApiResponse(
            code = 200,
            message = "노트 공유 리스트 조회 성공",
            data = noteShareService.getShareNoteList(SecurityUtil.getUserId(), projectId)
        )

    @DeleteMapping("/share/{shareNoteId}")
    fun deleteShareNote(@PathVariable shareNoteId: Long) =
        ApiResponse(
            code = 200,
            message = "공유 노트 삭제 성공",
            data = noteShareService.deleteShareNoteById(shareNoteId)
        )

}