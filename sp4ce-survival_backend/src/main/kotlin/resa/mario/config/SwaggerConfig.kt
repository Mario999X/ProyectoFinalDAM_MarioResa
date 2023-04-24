package resa.mario.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for Swagger
 *
 */
@Configuration
class SwaggerConfig {

    @Bean
    fun apiInfo(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Sp4ceSurvival-API")
                    .version(APIConfig.API_VERSION)
                    .description(
                        "Proyecto de fin de grado DAM: Sp4ce Survival -- Backend"
                    )
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Repositorio del proyecto en Github")
                    .url("https://github.com/Mario999X/ProyectoFinalDAM_MarioResa")
            )
    }

    @Bean
    fun httpApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("http")
            .pathsToMatch("${APIConfig.API_PATH}/**")
            .displayName("HTTP-API Sp4ceSurvival-API")
            .build()
    }
}