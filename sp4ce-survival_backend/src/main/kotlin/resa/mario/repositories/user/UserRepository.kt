package resa.mario.repositories.user

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import resa.mario.models.User
import java.util.*

@Repository
interface UserRepository : CoroutineCrudRepository<User, UUID> {
    fun findByUsername(username: String): Flow<User>
}