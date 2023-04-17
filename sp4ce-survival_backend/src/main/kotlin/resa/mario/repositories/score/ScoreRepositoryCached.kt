package resa.mario.repositories.score

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import resa.mario.models.Score
import resa.mario.repositories.user.UserRepository
import java.util.*

/**
 * Repository Class that will execute the Crud operations from the repository through the interface implemented
 *
 * @property repository [ScoreRepository]
 * @property userRepository [UserRepository]
 */
@Repository
class ScoreRepositoryCached
@Autowired constructor(
    private val repository: ScoreRepository,
    private val userRepository: UserRepository
) : IScoreRepository {

    /**
     * Function that will return the score from the database through an userID.
     *
     * @param userId
     * @return A possible [Score]
     */
    @Cacheable("scores")
    override suspend fun findByUserId(userId: UUID): Score? = withContext(Dispatchers.IO) {
        return@withContext repository.findByUserId(userId).firstOrNull()
    }

    /**
     * Function that will save the score into the database.
     *
     * @param score
     * @return [Score]
     */
    @CachePut("scores")
    override suspend fun save(score: Score): Score = withContext(Dispatchers.IO) {
        return@withContext repository.save(score)
    }

    /**
     * Function that will delete a score from the database through an UserID
     *
     * @param userId
     * @return A possible [Score]
     */
    @CacheEvict("scores")
    override suspend fun deleteByUserId(userId: UUID): Score? = withContext(Dispatchers.IO) {
        userRepository.findById(userId) ?: return@withContext null

        val score = repository.findByUserId(userId).firstOrNull()

        if (score != null) {
            repository.delete(score)
        }

        return@withContext score
    }
}