package resa.mario.repositories.user

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import resa.mario.models.User
import java.util.*

/**
 * Repository that will execute the CRUD operations with the database
 * Repository type: CoroutineCrudRepository
 *
 */
@Repository
interface UserRepository : CoroutineCrudRepository<User, UUID> {
    fun findByUsername(username: String): Flow<User>

    fun findByEmail(email: String): Flow<User>
}