# Sp4ce Survival Backend

Servicio orientado a la gestión del sistema online de almacenamiento de datos del videojuego Sp4ce Survival.

Proyecto final del Módulo Superior de Desarrollo de Aplicaciones Multiplataforma curso 22/23.

# Índice

- [Diseño](#diseño)
- [Estructura](#estructura)
- [Endpoints](#endpoints)
- [Funcionamiento](#funcionamiento)
- [Tests](#tests)
- [Autor](#autor)

# Diseño

## Introducción

Este servicio se ha diseñado usando [**PostgreSQL**](https://www.postgresql.org/) como base de datos relacional para el
almacenamiento de los datos relacionados con los *usuarios* y usando [**Spring**](https://spring.io/) como *framework*.

El proyecto fue creado haciendo uso de la herramienta online [Spring Initializr](https://start.spring.io/).

## Configuración del proyecto

La configuración del proyecto se realizó utilizando [Gradle](https://gradle.org/).

Se han usado las siguientes tecnologías:

- [Spring Data](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [PostgreSQL](https://www.postgresql.org/)
- [R2DBC](https://r2dbc.io/)
- [Corrutinas Kotlin](https://kotlinlang.org/docs/coroutines-overview.html)
- [Serialization Kotlin JSON](https://github.com/Kotlin/kotlinx.serialization)
- [Kotlin-Result](https://github.com/michaelbull/kotlin-result)
- [Dokka](https://github.com/Kotlin/dokka)
- [Swagger-SpringDoc-OpenAPI](https://springdoc.org/v2/)
- [JUnit 5](https://junit.org/junit5/)
- [MockK](https://mockk.io/)

## Configuración de la base de datos

La base de datos es ejecutada desde un [docker](https://www.docker.com/).

Las tablas son cargadas desde el archivo **schema.sql correspondiente** localizado en [Resources](./src/main/resources)
y los datos base son cargados cada vez que se ejecuta la aplicación desde la clase *main*, haciendo un reseteo a las
tablas si se lanza el *docker-compose de desarrollo*.

Según el despliegue de la aplicación, la base de datos tendra los puertos expuestos.

- Si se ejecuta el *docker-compose de desarrollo* (**dev**), la base de datos estara expuesta y se podra ejecutar la
  aplicación desde el IDE.
- Si se ejecuta el *docker-compose de producción* (**production**), la base de datos **no** estará expuesta y se
  trabajara a través de los puertos expuestos del propio *servicio*.

## Dominio

### Usuario

| Campo     | Tipo      | Descripción                                |
|-----------|-----------|--------------------------------------------|
| id        | UUID      | Identificador único.                       |
| username  | String    | Nombre del usuario.                        |
| password  | String    | Contraseña del usuario.                    |
| email     | String    | Email del usuario.                         |
| role      | UserRole  | Rol del usuario(USER, ADMIN).              |
| createdAt | LocalDate | Fecha de creación del usuario.             |

### Puntuación

| Campo        | Tipo      | Descripción                                  |
|--------------|-----------|----------------------------------------------|
| id           | UUID      | Identificador único.                         |
| userId       | UUID      | Identificador único del usuario relacionado. |
| scoreNumber  | Long      | Número de puntuación.                        |
| dateObtained | LocalDate | Fecha de obtención de la puntuación.         |

# Estructura

## Documentación

El proyecto ha sido ampliamente documentado en el archivo [PDF del proyecto](/docs/Proyecto_Desarrollo_de_aplicaciones_IES_Luis_Vives-Mario_Resa.pdf) en español, 
tanto la toma de decisiones en las tecnologías aplicadas como en el funcionamiento de las distintas clases y métodos.

Las clases se encuentran documentadas con *KDoc* en inglés, y usando una librería externa, **Dokka** se puede visualizar
en formato *HTML*.

Además, los *endpoints* se encuentran documentados con **Swagger**, visibles desde el
siguiente [enlace](https://localhost:6969/swagger-ui/index.html#/), con la aplicación en ejecución.

## Seguridad

Aplicando la actual última versión de la seguridad ofrecida por
Spring, [Spring Security V6](https://docs.spring.io/spring-security/reference/index.html), el uso de **SSL** y los
tokens personales (**JWT Auth0**), se garantiza una seguridad a la hora de guardar los datos de los distintos usuarios y
en la conexión e integridad del servicio.

# Endpoints

La ruta **base** del servicio: https://localhost:6969/sp4ceSurvival

## Tabla de endpoints

| Método | Endpoint(*base*)            		      | Auth | Descripción                                                    		       		          | Status Code(OK) | Return Content |
|--------|-------------------------------------|------|-------------------------------------------------------------------------------------|-----------------|----------------|
| POST   | /register                   		      | NO   | Registro de un usuario por si mismo.                           		       		          | 201             | JSON           |
| POST   | /create                     		      | JWT  | Creación de un usuario por parte de un administrador.          		       		          | 201             | JSON           |
| GET 	  | /login                      		      | NO   | Iniciar sesión usando el nombre de usuario y la contraseña.    		       		          | 200             | JSON           |
| GET    | /username?username=X        		      | JWT  | Búsqueda de un usuario usando su nombre de usuario.            		       		          | 200             | JSON           |
| GET    | /leaderboard?page=X&size=Y&sortBy=Z | JWT  | Obtención de usuarios con puntuación y su posición según esta. 		       		          | 200             | JSON           |
| GET    | /me                                 | JWT  | Obtención de la información propia por parte de un usuario.    		       		          | 200             | JSON           |
| PUT    | /me/score                           | JWT  | Almacenar o actualizar la puntuación obtenida por parte de un usuario.          		  | 200             | JSON           |
| PUT    | /me/password                        | JWT  | Actualizar la contraseña actual por parte de un usuario.		               		         | 200             | JSON           |
| DELETE | /me        				                     | JWT  | Eliminación de una cuenta y su posible puntuación asociada por parte de un usuario. | 204             | -       	      |
| GET    | /score			                           | JWT  | Búsqueda de una posible puntuación por parte de un usuario.		                       | 200             | JSON           |

# Funcionamiento

Para probar el funcionamiento de los distintos endpoints durante el desarrollo se usó un cliente que permita recibir y
enviar *request-response*, por ejemplo:

- [Postman](https://www.postman.com/).
- [ThunderClient](https://www.thunderclient.com/) como *plugin* en [VSC](https://code.visualstudio.com/).

Cuando el desarrollo se complete, se usará desde un videojuego actuando como cliente.

# Tests

Se han testeado los repositorios, el servicio y el controlador, usando **JUnit 5** y **MockK**

Se han testeado los endpoints usando **Postman** "E2E"; se adjunta el *JSON* exportado en la carpeta *postman* en la
raíz del proyecto.

- **AVISO**: Los **tokens** usados para las distintas operaciones han sido almacenados como *variables de entorno*, por
  lo que pueden surgir problemas al tratar de ejecutar el JSON en *ThunderClient*.

# Autor

[Mario Resa](https://github.com/Mario999X)

