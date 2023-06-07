# Sp4ce Survival Frontend

Videojuego de género Bullet Hell y con capacidades online competitivas.

Proyecto final del Módulo Superior de Desarrollo de Aplicaciones Multiplataforma curso 22/23. IES Luis Vives.

## Índice

- [Introducción](#introducción)
- [Documentación](#documentación)
- [Core Gameplay](#core-gameplay)
- [Controles](#controles)
- [Atribuciones](#atribuciones)
- [Autor](#autor)

## Introducción

El videojuego se ha diseñado usando [Godot](https://godotengine.org/), en su versión más estable actualmente **(3.5.2)**, y usando [GDScript](https://gdscript.com/) como lenguaje de programación.

En este repositorio, se podrá observar el código fuente de la aplicación.

## Documentación

En el documento [PDF del proyecto](/docs/Proyecto_Desarrollo_de_aplicaciones_IES_Luis_Vives-Mario_Resa.pdf), 
se ha escrito un apartado estilo GDD que documenta el videojuego en sus aspectos generales y decisiones de diseño tomadas.

En **Godot** no existe un formato específico de documentar el código, como **JavaDoc o Kdoc**, por lo que se ha tratado de explicar brevemente valores o funciones clave con simples comentarios.

## Core Gameplay

La actividad principal del juego será aguantar el mayor tiempo posible evitando cualquier contacto con los enemigos.

El jugador podrá disparar proyectiles que, podrán eliminar un tipo de balas enemigas y a todos los enemigos en sí mismos. Otro tipo de proyectiles solo podrán ser esquivados.

El jugador dispondrá de balas infinitas, pero un cargador limitado, que obligará a tener cierta cautela a la hora de abrir fuego.

El tiempo límite proporcionado será de 90 segundos.

La puntuación subirá de la siguiente manera:
- Destruir bala enemiga individual: 3 puntos
- 1 segundo resistido: 1 punto
- Destruir nave enemiga individual: 10 puntos
- Resistir los 90 segundos al completo: 1000 puntos adicionales (siempre antes de aplicar el multiplicador de dificultad).

Los niveles de dificultad afectan de forma grave al aguante del jugador, pero también a la puntuación final obtenida:
- Fácil: 3 Golpes – x2 a la puntuación.
- Medio: 2 Golpes – x4 a la puntuación.
- Difícil: 1 Golpe – x7 a la puntuación.

## Controles

### Teclado y Ratón

- **W**: Desplazamiento hacia arriba.
- **A**: Desplazamiento hacia la derecha.
- **S**: Desplazamiento hacia abajo.
- **D**: Desplazamiento hacia la izquierda.

- **Esc**: Pausar.

- **Clic Izquierdo del Ratón**: Disparar.

## Atribuciones

### Itch.io

- <a href="https://gamesupply.itch.io/ultimate-space-game-mega-asset-package" title="Space Mega Asset Package"> Ultimate Space Game Mega Asset Package by Game Supply Guy - Itch.io</a>

- <a href="https://deep-fold.itch.io/space-background-generator" title="Space Background Generator"> Space Background Generator by Deep-Fold - Itch.io</a>

### Flaticon

- <a href="https://www.flaticon.com/free-icons/loading" title="loading icons">Loading icons created by th studio - Flaticon</a>
    - [Icono Específico](https://www.flaticon.com/free-icon/sync_2767294?term=loading&page=1&position=13&origin=tag&related_id=2767294)
- <a href="https://www.flaticon.com/free-icons/space" title="space icons">Space icons created by Freepik - Flaticon</a>
    - [Icono Específico](https://www.flaticon.com/free-icon/galaxy_3919942)

## Autor

[Mario Resa](https://github.com/Mario999X)

