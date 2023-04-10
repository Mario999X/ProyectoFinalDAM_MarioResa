package resa.mario.repositories.score

import org.springframework.stereotype.Repository
import resa.mario.models.Score
import java.util.*

@Repository
interface IScoreRepository {
    suspend fun findByUserId(userId: UUID): Score?
    suspend fun save(score: Score): Score
    suspend fun deleteByUserId(userId: UUID): Score?
}