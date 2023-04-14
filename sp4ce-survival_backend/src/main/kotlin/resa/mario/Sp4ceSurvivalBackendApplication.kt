package resa.mario

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import resa.mario.controllers.UserController
import resa.mario.db.getScoresInit
import resa.mario.db.getUsersInit
import resa.mario.models.User

/**
 * @author Mario Resa
 *
 * @property controller UserController for InitializerMethods
 */
@SpringBootApplication
@EnableR2dbcRepositories
@EnableCaching
class Sp4ceSurvivalBackendApplication
@Autowired constructor(private val controller: UserController) : CommandLineRunner {
    override fun run(vararg args: String?) = runBlocking {
        println("STARTING API REST...")

        val users = mutableListOf<User>()

        getUsersInit().forEach {
            val user = controller.createUserInitializer(it)
            users.add(user!!)
        }

        var i = 0
        getScoresInit().forEach {
            controller.createScoreInitializer(users[i].id!!, it)
            i++
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Sp4ceSurvivalBackendApplication>(*args)
}
