package resa.mario.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

/**
 * Class for loading automatically the schema-dev.sql from Resources when executing the application.
 *
 */
@Configuration
class LoadSchema {

    @Value("\${schema.file}")
    private val schemaFile: String? = null

    @Bean
    fun initializer(@Qualifier("connectionFactory") connectionFactory: ConnectionFactory?): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        if (connectionFactory != null) {
            initializer.setConnectionFactory(connectionFactory)
        }
        val resource = ResourceDatabasePopulator(ClassPathResource(schemaFile!!))
        initializer.setDatabasePopulator(resource)
        return initializer
    }

}