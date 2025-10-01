package project.spring.project_manager_be.socket.handler

import org.springframework.stereotype.Component
import project.spring.project_manager_be.note.dto.NoteShareEditDto
import project.spring.project_manager_be.note.entity.NoteEntity
import project.spring.project_manager_be.note.service.NoteService
import java.time.LocalDateTime
import java.time.ZoneOffset
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff


@Component
class NoteEditHandler(
    private val noteService: NoteService
) {
    //병합을 위한 Map 추가
    private val noteEditMap: MutableMap<Long, NoteShareEditDto> = mutableMapOf()


    fun processNoteUpdate(noteId: Long, requestUserId: Long, raw: String): NoteEntity {
        //println("호출")
        val now: LocalDateTime = LocalDateTime.now()
        val timestamp: Long = now.toInstant(ZoneOffset.UTC).toEpochMilli()
        val mergeNoteRaw = getUpdateNoteRaw(noteId, requestUserId, raw, timestamp)

        return noteService.updateNoteRaw(noteId, mergeNoteRaw) //노트 내용을 업데이트
    }

    fun getUpdateNoteRaw(noteId: Long, requestUserId: Long, raw: String, currentTime: Long) : String {
        val mergeTime = 5000 //시간 보면서 체크하기
        val previousNote = noteEditMap[noteId]

        if (previousNote != null && currentTime - previousNote.editTime < mergeTime) {
            //println("병합 진입")
            val updateNoteRaw = mergeNote(previousNote.raw , raw) //병합한 뒤에 소비 해야될거같은데?
            noteEditMap.remove(noteId) //기존 노트 정보는 소비
            return updateNoteRaw
        } else {
            //println("노트 값이 없어요 형님")
            noteEditMap[noteId] = NoteShareEditDto(editTime = currentTime, userId = requestUserId, raw = raw)
            return raw
        }
    }

    fun mergeNote(previousNoteRaw: String, afterNoteRaw: String): String {
        val dmp = DiffMatchPatch()
        val diffs = dmp.diffMain(previousNoteRaw, afterNoteRaw)
        dmp.diffCleanupMerge(diffs)

        val result = StringBuilder()

        diffs.forEach { diff ->
            when (diff.operation) {
                DiffMatchPatch.Operation.EQUAL -> result.append(diff.text)
                DiffMatchPatch.Operation.INSERT -> result.append(diff.text) // 새로운 내용 추가
                DiffMatchPatch.Operation.DELETE -> {
                    // 삭제된 내용은 주석으로 보존
                    result.append("<!-- 삭제됨: ${diff.text} -->")
                }
            }
        }

        return result.toString()
    }

}