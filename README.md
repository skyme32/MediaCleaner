# MediaCleaner

Aplicación de escritorio (**Kotlin Multiplatform / Compose Desktop**) para normalizar y renombrar automáticamente ficheros de series y películas, eliminando etiquetas técnicas del nombre (`720p`, `BluRay`, `x264`, etc.) y estandarizando el formato de temporada/episodio a `title - SxxEyy`. Aplicación hecha totalmente por IA, probando la potencia de tal.

---

## ✨ Características

| Funcionalidad | Descripción |
|---|---|
| 🧹 Limpieza de títulos | Elimina ruido técnico, normaliza separadores y detecta temporada/episodio |
| 📁 Renombrado en disco | Procesa carpetas de forma recursiva (opcional) usando **Okio** |
| 👁️ Vista previa | Prueba el resultado del limpiador en tiempo real sin tocar el disco |
| 🗃️ Historial | Base de datos local (**SQLDelight / SQLite**) con todos los renombrados realizados |
| ⚙️ Ajustes | Lista de términos de ruido editable y persistida con **Multiplatform Settings** |

---

## 🏗️ Arquitectura

El proyecto sigue **Clean Architecture** + principios **SOLID**, dividido en tres capas:

### Pipeline de limpieza (`TitleCleanerImpl`)

El método `clean()` es un pipeline funcional inmutable de 8 pasos sobre un acumulador `ParsedName`:---

## 🛠️ Tecnologías

| Librería | Uso |
|---|---|
| [Compose Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform.html) | UI de escritorio |
| [Okio](https://square.github.io/okio/) | Lectura/escritura de ficheros en disco |
| [SQLDelight](https://cashapp.github.io/sqldelight/) | Base de datos SQLite multiplataforma |
| [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings) | Persistencia de preferencias del usuario |
| [AndroidX Lifecycle ViewModel](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-lifecycle.html) | ViewModel con `StateFlow` |

---

## 🚀 Ejecutar la aplicación

> **Requisito:** Java 17 o superior.

```shell
# macOS / Linux
./gradlew :composeApp:run

# Windows
.\gradlew.bat :composeApp:run# .deb (Linux), .msi (Windows), .dmg (macOS)
./gradlew :composeApp:packageDistributionForCurrentOS720p
1080p
bluray
x264
[a-z]{2,}\.(com|net|org|to)
````

## 🚀 Crear build
```shell
./gradlew packageDistributionForCurrentOS
```

## 🚀 Crear jar
```shell
./gradlew packageUberJarForCurrentOS
```
