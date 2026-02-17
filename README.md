# MirageWar

## Estado de desarrollo:

En proceso de la segunda etapa(creacion de funciones y parte logica del juego, comienzo de parte grafica, modelos de unidades, estructuras y mapa).

## Integrantes: 

-Santino Giuffrida

-Nahuel Linares



## Descripcion breve del juego: 
El juego trata de conquistar todo el continente de Feronis, derrotando a distintas facciones y consiguiendo mejoras a lo largo del juego.

## Tecnologias principales y Plataformas: 

-LIBGDX

-Software TILERD

-lenguaje: Java

-IDE: IntelliJ IDEA

-PC (Windows)

## Wiki: [pagina de la WIKI](https://github.com/NahuelLV/TRABAJOFINAL/wiki)

##  Instrucciones Básicas de Compilación y Ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/NahuelLV/TRABAJOFINAL.git
cd TRABAJOFINAL
```

### 2. Requisitos previos
Java JDK 8 o superior (recomendado: JDK 11)

Gradle (opcional, se incluye el wrapper)

Eclipse, IntelliJ IDEA o cualquier editor con terminal

### 3. Compilar el proyecto
En Windows
```bash
gradlew.bat desktop:build
```
### 4. Ejecutar el juego
En Windows
```bash
gradlew.bat desktop:run
```

### Instrucciones adicionales

#### Ejecutar desde un entorno de desarrollo (IDE)
##### IntelliJ IDEA

1. Abrí IntelliJ IDEA.

2. Seleccioná la opción "Open" y elegí la carpeta raíz del proyecto (TRABAJOFINAL).

3. IntelliJ reconocerá el proyecto como Gradle y lo configurará automáticamente.

4. Una vez cargado, buscá la clase Lwjgl3Launcher.java dentro del módulo lwjgl3 o desktop.

5. Hacé clic derecho sobre la clase y seleccioná Run 'Lwjgl3Launcher.main()'.

##### Eclipse

1. Abrí Eclipse.

2. Seleccioná File > Import > Gradle > Existing Gradle Project.

3. Navegá hasta la carpeta del proyecto (TRABAJOFINAL) y completá la importación.

4. Una vez importado, buscá Lwjgl3Launcher.java y ejecutalo como aplicación Java (Run As > Java Application).

### Comandos para Linux/macOS
Todos los comandos que usan gradlew.bat en Windows, deben ejecutarse así en sistemas Unix (Linux/macOS):

-Compilar el proyecto
```bash
./gradlew desktop:build
```
-Ejecutar el juego
```bash
./gradlew desktop:run
```
