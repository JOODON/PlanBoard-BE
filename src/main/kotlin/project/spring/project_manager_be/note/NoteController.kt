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
import project.spring.project_manager_be.utill.ApiResponse

@RestController
@RequestMapping("/api/notes")
class NoteController(
    private val nodeService : NoteService
) {

    @GetMapping("/{projectId}")
    fun noteList(@PathVariable projectId : Long) =
        ApiResponse(code = 200, message = "노트 목록 조회 성공", data = nodeService.findProjectNoteList(projectId))

    @PostMapping
    fun addNote(@RequestHeader userId : Long, @RequestBody noteRequest: NoteRequest) =
        ApiResponse(code = 200, message = "프로젝트 조회 성공", data = nodeService.createNode(userId , noteRequest))

    @PutMapping("/{noteId}")
    fun updateNote(@RequestHeader userId: Long, @PathVariable noteId: Long, @RequestBody noteRequest: NoteRequest) =
        ApiResponse(code = 200, message = "프로젝트 업데이트 성공", data = nodeService.updateNote(userId, noteId, noteRequest))

    @DeleteMapping("/{noteId}")
    fun deleteNote(@RequestHeader userId: Long, @PathVariable noteId: Long) =
        ApiResponse(code = 200, message = "프로젝트 삭제 성공", data = nodeService.deleteNoteById(userId, noteId))

    @GetMapping("/{noteId}/share")
    fun shareNote(@RequestHeader userId: Long, @PathVariable noteId: Long) =
        ApiResponse(code = 200, message = "노트 공유 링크 생성 성공" , data = nodeService.createShareNoteUrl(userId, noteId))

}