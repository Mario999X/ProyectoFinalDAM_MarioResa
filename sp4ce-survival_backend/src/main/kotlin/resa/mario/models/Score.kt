package resa.mario.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.UUID

/**
 * Score model
 *
 * @property id Main ID, UUID
 * @property userId Foreing Key of User ID, UUID
 * @property scoreNumber Number of the score obtained, Long
 * @property dateObtained Date Score obtained, LocalDate
 */
@Table(name = "scores")
data class Score(
    @Id
    val id: UUID? = null,
    @Column("user_id")
    val userId: UUID,
    @Column("score_number")
    val scoreNumber: Long,
    @Column("date_obtained")
    val dateObtained: LocalDate
)
