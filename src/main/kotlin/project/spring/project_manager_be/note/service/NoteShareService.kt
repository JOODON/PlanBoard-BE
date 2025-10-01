package project.spring.project_manager_be.note.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.spring.project_manager_be.config.AppConfig
import project.spring.project_manager_be.note.dto.NoteDto
import project.spring.project_manager_be.note.entity.NoteShareEntity
import project.spring.project_manager_be.note.http.NoteShareRequest
import project.spring.project_manager_be.note.repository.NoteRepository
import project.spring.project_manager_be.note.repository.NoteShareRepository
import project.spring.project_manager_be.note.repository.NoteTagRepository
import project.spring.project_manager_be.user.UserService
import project.spring.project_manager_be.utill.CryptoUtil

@Service
class NoteShareService(
    private val userService: UserService,
    private val noteService: NoteService,

    private val noteRepository: NoteRepository,
    private val noteTagRepository: NoteTagRepository,
    private val noteShareRepository: NoteShareRepository,

    private val appConfig: AppConfig
) {

    fun getShareNoteList(userId : Long, projectId: Long) : List<NoteDto> {
        val noteShareList = noteShareRepository.findByUserIdAndProjectId(userId, projectId)
        if (noteShareList.isEmpty()) return emptyList()

        val noteIdList = noteShareList.map { it.noteId }
        if (noteIdList.isEmpty()) return emptyList() // 안전 처리

        val noteList = noteRepository.findByIdIn(noteIdList)        //노트 List 가지고오기
        val tagList = noteTagRepository.findByNoteIdIn(noteIdList)  //태그 List 가지고오기

        val noteTagsGroup = tagList.groupBy { it.noteId }
        val shareGroup = noteShareList.associateBy { it.noteId }

        return noteList.map { note ->
            NoteDto.toDto(
                note,
                noteTagsGroup[note.id]
                    ?: emptyList(),
                shareGroup[note.id]
            )
        }
    }

    @Transactional
    fun createSharedNote(userId: Long, noteShareRequest: NoteShareRequest): Map<String, String> {
        noteService.hasNote(noteShareRequest.noteId)
        return mapOf("url" to getOrCreateNoteShareUrl(userId, noteShareRequest.noteId, noteShareRequest.projectId))
    }

    //노트 생성 하거나 기존에 있으면 반환하는 로직
    @Transactional
    fun getOrCreateNoteShareUrl(userId: Long, noteId: Long, projectId : Long): String {
        return noteShareRepository.findByUserIdAndNoteIdAndProjectId(userId, noteId, projectId)?.shareUrl
            ?: run {
                val connUser = userService.findUserById(userId)
                val wsUrl = createNoteShareUrl(userId, noteId)
                saveNoteShareUrl(
                    NoteShareEntity.toNoteShareEntity(
                        userId = connUser.id!!,
                        projectId = projectId,
                        noteId = noteId,
                        username = connUser.name,
                        shareUrl = wsUrl
                    )
                ).shareUrl
            }
    }

    //노트 생성
    fun createNoteShareUrl(userId: Long, noteId: Long): String {
        val payload = "${userId}_x_${noteId}"              // 구분자 _x_ 사용
        val encrypted = CryptoUtil.encrypt(payload)       // 암호화
        val baseApi = appConfig.serverBaseUrl
            .removePrefix("http://")
            .removePrefix("https://")
            .removeSuffix("/")  // 맨 끝 슬래시 제거

        return "ws://$baseApi/project-manager/ws/note?code=$encrypted"
    }

    //공유 노트 저장
    fun saveNoteShareUrl(noteShareEntity: NoteShareEntity) =
        noteShareRepository.save(noteShareEntity)

    //공유 노트 삭제
    fun deleteShareNoteById(shareNoteId: Long) =
        noteShareRepository.deleteById(shareNoteId)



}