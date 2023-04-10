package resa.mario.repositories.score

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import resa.mario.models.Score
import java.util.UUID

@Repository
interface ScoreRepository : CoroutineCrudRepository<Score, UUID> {
    fun findByUserId(userId: UUID): Flow<Score>

    fun findAllBy(page: Pageable?): Flow<Score>
}