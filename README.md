# ProyectoFinalDAM_MarioResa

Proyecto final de Desarrollo de Aplicaciones Multiplataforma del curso 22/23. IES Luis Vives.

## Índice

- [Introducción](#introducción)
- [Plataformas Soportadas](#plataformas-soportadas)
- [Diseño](#diseño)
- [Funcionamiento de la Aplicación](#funcionamiento-de-la-aplicación)
- [Autor](#autor)

## Introducción

El gran proyecto es un videojuego con capacidades competitivas online (sin ser multijugador) usando una tabla de
puntuaciones que cualquier usuario registrado pueda ver dentro de este.

Este proyecto se encuentra formado por dos partes, clásicas en un desarrollo de software, backend y frontend.

¿Y... el nombre elegido? **¡Sp4ce Survival!**

Se basará en un bullet hell orientado a la supervivencia del jugador donde su principal objetivo será obtener la mayor
puntuación posible.

Si se quiere descargar el videojuego en formato de ejecutable, se realizará a través del siguiente enlace:

- [Coming Soon]

## Plataformas Soportadas
 ![Windows](https://img.shields.io/badge/Windows-0078D6?style=for-the-badge&logo=windows&logoColor=white)
 
<!-- ![Mac OS](https://img.shields.io/badge/mac%20os-000000?style=for-the-badge&logo=apple&logoColor=F0F0F0) -->

 ![Linux](https://img.shields.io/badge/Linux-FCC624?style=for-the-badge&logo=linux&logoColor=black)

## Diseño

### [Diagramas](diagrams)

#### Diagrama de Clases

<img align="center" src="diagrams/Class_diagram.png">

Se observan dos elementos principales del proyecto, *Usuario* y *Puntuación*.

La relación es sencilla, un usuario puede no tener una puntuación asociada, o **una** como **máximo**.

Además, exiten **dos** roles para los usuarios:

- USER
- ADMIN

#### Diagrama de Entidad-Relación

<img align="center" src="diagrams/Entity_Relationship_diagram.png">

Un usuario obtiene una puntuación y la puede almacenar/actualizar.

## Funcionamiento de la aplicación

Realmente ambas partes son elementos individuales que forman parte de algo mayor, aunque el videojuego podrá ser
disfrutado de manera individual debido a que se ofrece soporte offline.

Dejando eso de lado, la ejecución del programa se produciría en el siguiente orden, en especial, refiriéndome a los
posibles **menús** que puede acceder el usuario, y a las capacidades que ofrecen:

### Menú de bienvenida

El usuario podría iniciar sesión, registrarse o jugar offline; si decidió jugar online, y una vez verificada la acción
realizada con el backend, este pasaría al menú principal.

Además, cuenta con el menú de opciones y la posibilidad de cerrar la aplicación.

### Menú principal

El usuario podrá jugar, con la capacidad de elegir un nivel de dificultad; ver su perfil y ver una tabla de
puntuaciones.

Además, cuenta con el menú de opciones y la posibilidad de cerrar la aplicación (además,
si la última vez que salió se encontraba en modo online, se realizará una comprobación del estado de la cuenta y si todo
es correcto, se pasará automáticamente a este menú sin pasar por el de bienvenida, si no, el estado online se pasará a offline y se
dejará al usuario en el menú de bienvenida).

### Menú de perfil - [Solo Online]

[Coming Soon]

### Menú tabla de puntuaciones - [Solo Online]

[Coming Soon]

### Menú de opciones

Es un sencillo menú que permite ajustar ciertos valores del juego y es accesible desde tres localizaciones diferentes:

- Menú de bienvenida
- Menú principal
- Menú de pausa en el nivel jugable

### Nivel Jugable

El usuario podrá jugar una partida, y al finalizarla obtendra cierta puntuación, si se encuentra en modo online, se
obtendrá
la puntuación actual de este, y se comparará, si la antigua es superior a la nueva, no se hará nada, pero si la obtenida
es
superior, esta será almacenada en la base de datos. Este proceso se realizará de forma automática.

Según la dificultad seleccionada, la puntuación final será multiplicada por un valor distinto.

Siempre que finalice una partida, se le mostrará un sencillo menú donde se permitirá, o bien jugar de nuevo, o volver al
menú principal.

## Autor

[Mario Resa](https://github.com/Mario999X)

