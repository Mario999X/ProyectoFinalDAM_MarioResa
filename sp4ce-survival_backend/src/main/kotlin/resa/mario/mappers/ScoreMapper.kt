package resa.mario.mappers

import resa.mario.dto.ScoreDTOCreate
import resa.mario.dto.ScoreDTOResponse
import resa.mario.models.Score
import java.time.LocalDate
import java.util.*

/**
 * Mapper for [ScoreDTOCreate] to [Score]
 *
 * @return [Score]
 */
fun ScoreDTOCreate.toScore(): Score {
    return Score(
        userId = UUID.fromString(userId),
        scoreNumber = scoreNumber.toLong(),
        dateObtained = LocalDate.now()
    )
}

/**
 * Mapper for [Score] to [ScoreDTOResponse]
 *
 * @return [ScoreDTOResponse]
 */
fun Score.toScoreDTO(): ScoreDTOResponse {
    return ScoreDTOResponse(
        scoreNumber = scoreNumber.toString(),
        dateObtained = dateObtained.toString()
    )
}