package resa.mario.dto

import kotlinx.serialization.Serializable

@Serializable
data class ScoreDTOCreate(
    val userId: String,
    val scoreNumber: String
)

@Serializable
data class ScoreDTOResponse(
    val scoreNumber: String,
    val dateObtained: String
)