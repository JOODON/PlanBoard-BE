package project.spring.project_manager_be.ocr

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import project.spring.project_manager_be.utill.ApiResponse

@RestController
@RequestMapping("/api/ocr")
class OcrController(
    private val ocrService: OcrService
) {

    @PostMapping("/extract")
    fun extractTextFromImage(
        @RequestHeader userId : Long,
        @RequestParam("file") file: MultipartFile
    ) =
        ApiResponse(code = 200, message = "프로젝트 삭제 성공", data = ocrService.extractText(file))


}