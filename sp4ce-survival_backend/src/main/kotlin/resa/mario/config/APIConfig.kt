package resa.mario.config

import org.springframework.context.annotation.Configuration

/**
 * API basic configuration using the different constants in other classes.
 *
 */
@Configuration
class APIConfig {
    companion object {
        const val API_PATH = "/sp4ceSurvival"

        const val API_VERSION = "1.0.0"

        const val PAGINATION_INIT = "0"

        const val PAGINATION_SIZE = "10"

        const val PAGINATION_SORT = "scoreNumber"
    }
}