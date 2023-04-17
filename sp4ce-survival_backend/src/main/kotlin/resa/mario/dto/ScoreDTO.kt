package resa.mario.dto

import kotlinx.serialization.Serializable

/**
 * DTO for the creation/update of a score
 *
 * @property userId ID of the associated user, String
 * @property scoreNumber Number of the score, String
 */
@Serializable
data class ScoreDTOCreate(
    val userId: String,
    val scoreNumber: String
)

/**
 * DTO for a generic Score Request
 *
 * @property scoreNumber Number of the score, String
 * @property dateObtained Date Score obtained, String
 */
@Serializable
data class ScoreDTOResponse(
    val scoreNumber: String,
    val dateObtained: String
)