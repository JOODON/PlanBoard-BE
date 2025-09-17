package project.spring.project_manager_be.user

import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun findOrCreateUser(userEntity: UserEntity): UserEntity {
        userEntity.phone = formatPhoneNumber(userEntity.phone);
        userEntity.birth = formatBirth(userEntity.birth)

        return findUserByNameAndPhoneBirth(
            name = userEntity.name,
            phone = userEntity.phone,
            birth = userEntity.birth
        ) ?: createUser(userEntity)
    }

    fun findUserById(userId: Long): UserEntity =
        userRepository.findById(userId)
        .orElseThrow { IllegalArgumentException("해당 유저 정보는 존재하지 않습니다") }


    fun createUser(userEntity: UserEntity) =
        userRepository.save(userEntity)

    //유저 전화번호 포멧
    private fun formatPhoneNumber(phone: String): String {
        // 숫자만 남기기
        val digits = phone.filter { it.isDigit() }

        return when (digits.length) {
            10 -> "${digits.substring(0,3)}-${digits.substring(3,6)}-${digits.substring(6,10)}"
            11 -> "${digits.substring(0,3)}-${digits.substring(3,7)}-${digits.substring(7,11)}"
            else -> throw IllegalArgumentException("잘못된 전화번호 형식: $phone")
        }
    }

    private fun formatBirth(birth: String): String {
        // 숫자만 남기기
        val digits = birth.filter { it.isDigit() } //문자 하나가 숫자인지 아닌지 판별해주는 친구

        return when (digits.length) {
            8 -> "${digits.substring(0,4)}-${digits.substring(4,6)}-${digits.substring(6,8)}" // YYYY-MM-DD
            6 -> "${digits.substring(0,2)}-${digits.substring(2,4)}-${digits.substring(4,6)}" // YY-MM-DD
            else -> throw IllegalArgumentException("잘못된 생년월일 형식: $birth")
        }
    }

    private fun findUserByNameAndPhoneBirth(
        name: String,
        phone: String,
        birth: String
    ): UserEntity? = userRepository.findUserByNameAndPhoneAndBirth(
        name = name,
        phone = phone,
        birth = birth
    )

}