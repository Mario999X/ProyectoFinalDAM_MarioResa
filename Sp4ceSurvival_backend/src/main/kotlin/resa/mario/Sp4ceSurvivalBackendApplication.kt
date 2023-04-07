package resa.mario

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories
@EnableCaching
class Sp4ceSurvivalBackendApplication : CommandLineRunner {
	override fun run(vararg args: String?) {
		println("STARTING API REST...")

	}
}

fun main(args: Array<String>) {
	runApplication<Sp4ceSurvivalBackendApplication>(*args)
}
