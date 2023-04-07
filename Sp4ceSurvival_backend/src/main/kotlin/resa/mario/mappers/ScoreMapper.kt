package resa.mario.mappers

import resa.mario.dto.ScoreDTOCreate
import resa.mario.dto.ScoreDTOResponse
import resa.mario.models.Score
import java.time.LocalDate
import java.util.*


fun ScoreDTOCreate.toScore(): Score {
    return Score(
        userId = UUID.fromString(userId),
        scoreNumber = scoreNumber.toLong(),
        dateObtained = LocalDate.now()
    )
}

fun Score.toScoreDTO(): ScoreDTOResponse {
    return ScoreDTOResponse(
        scoreNumber = scoreNumber.toString(),
        dateObtained = dateObtained.toString()
    )
}