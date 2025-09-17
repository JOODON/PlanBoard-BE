package project.spring.project_manager_be.utill

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
object ByteUtil {

    @JvmStatic
    @Value("\${note-max-size:3145728}") // 5MB 기본값
    var MAX_NOTE_SIZE: Long = 3 * 1024 * 1024

}