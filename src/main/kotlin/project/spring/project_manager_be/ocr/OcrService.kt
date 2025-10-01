package project.spring.project_manager_be.ocr

import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.TesseractException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import project.spring.project_manager_be.config.AppConfig
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Service
class OcrService(
    private val appConfig: AppConfig,
) {
    //환경 설정에 맞게 properties 설정
    private val tesseract: Tesseract = Tesseract().apply {
        when(appConfig.activeProfile){
            "local"->{
                System.setProperty("jna.library.path", "/opt/homebrew/Cellar/tesseract/5.5.1/lib")
                setDatapath("/opt/homebrew/share/tessdata")
            }
            "prod" -> {
                // JNA 네이티브 라이브러리 경로 (Ubuntu 기본 위치)
                System.setProperty("jna.library.path", "/usr/lib/x86_64-linux-gnu")
                setDatapath("/usr/share/tesseract-ocr/5/tessdata")
            }
        }

        setLanguage("kor+eng") //언어 설정
        setPageSegMode(3)      // 완전 자동
        setOcrEngineMode(1)    // LSTM 엔진
    }

    fun extractText(file: MultipartFile): Map<String, String> {
        val tempFile = File.createTempFile("ocr_temp_", file.originalFilename)

        return try {
            file.transferTo(tempFile)

            // 이미지 전처리로 정확도 향상
            val preprocessedImage = preprocessImage(tempFile)
            val result = tesseract.doOCR(preprocessedImage)
            val sanitizedResult = sanitizeOcrResult(result)

            mapOf("text" to sanitizedResult)

        } catch (e: TesseractException) {
            e.printStackTrace()
            mapOf("text" to "OCR 처리 중 오류 발생: ${e.message}")

        } catch (e: Exception) {
            e.printStackTrace()
            mapOf("text" to "파일 처리 중 오류 발생: ${e.message}")

        } finally {
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }
    fun sanitizeOcrResult(result: String): String {
        return result.lines().joinToString("\n") { line ->
            val codeIndicators = listOf(";", "{", "}", "(", ")", "[", "]", "=")
            val isCodeLine = codeIndicators.count { line.contains(it) } >= 2
            if (isCodeLine) line.trim() // 코드: 그대로
            else line.replace(Regex("[^가-힣A-Za-z0-9 .,!?\\-]"), "").trim() // 텍스트: 필터링
        }
    }
    // 이미지 전처리 함수
    private fun preprocessImage(file: File): BufferedImage {
        val original = ImageIO.read(file)
        val grayscale = BufferedImage(original.width, original.height, BufferedImage.TYPE_BYTE_GRAY)
        val g2d = grayscale.createGraphics()
        g2d.drawImage(original, 0, 0, null)
        g2d.dispose()
        return grayscale
    }

}
