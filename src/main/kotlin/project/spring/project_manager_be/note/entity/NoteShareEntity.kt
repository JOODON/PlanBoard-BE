package project.spring.project_manager_be.note.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "tb_note_share")
class NoteShareEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "note_id")
    val noteId: Long,

    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "username")
    val username: String,

    @Column(name = "share_url")
    val shareUrl: String
)
{
}