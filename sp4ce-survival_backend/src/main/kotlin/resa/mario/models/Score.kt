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
    @Column("UserId")
    val userId: UUID,
    @Column("ScoreNumber")
    val scoreNumber: Long,
    @Column("DateObtained")
    val dateObtained: LocalDate
)
