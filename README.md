# Prototipo ST Móvil: Implementación de Intents y Persistencia ✨

## 🧑‍💻 Resumen y Versión
[cite_start]Este proyecto es el segundo prototipo desarrollado para la asignatura **Programación Android**, siguiendo los lineamientos de Santo Tomás[cite: 2]. [cite_start]El objetivo principal fue construir una aplicación que incorpore la comunicación entre componentes (Intents) y la persistencia de datos[cite: 7, 62].

[cite_start]Hemos implementado un total de **8 Intents** (5 implícitos y 3 explícitos) y validaciones robustas para la experiencia de usuario[cite: 24, 17].

* **Tecnología:** Java / Android Studio
* **Versión Android Mínima:** API 26 (Android Oreo)
* **Versión AGP:** [**COLOCAR VERSIÓN DE AGP AQUÍ**]

## 🛠️ Intents Implementados (8/8)

[cite_start]A continuación se detalla el listado de los 5 Intents Implícitos y 3 Explícitos implementados, esenciales para el desarrollo de la actividad[cite: 25, 26].

### I. Intents Explícitos (3/3)
Estos Intents gestionan la navegación interna entre las Activities del propio proyecto.

| No. | Flujo | Descripción / Acción | Criterio (Referencia) |
| :---: | :--- | :--- | :--- |
| 1 | `Splash` → `Login` | Navegación simple al iniciar la aplicación. | Punto de entrada. |
| 2 | `Login` → `Home` | Navegación al ingresar credenciales correctas. **Pasa el email** como extra. | [cite_start]`MainActivity` $\rightarrow$ `Detalle Activity (con datos extra)` [cite: 32] |
| 3 | `Home` $\rightarrow$ `Perfil` | Abre la pantalla de edición de usuario y **retorna el nuevo nombre** usando `registerForActivityResult()`. | [cite_start]`FormActivity` $\rightarrow$ `ConfirmActivity (con resultado)` [cite: 32] |
| 4 | `Home` $\rightarrow$ `Configuración` | Abre la pantalla de ajustes para guardar URL y teléfono en `SharedPreferences`. | [cite_start]`MainActivity` $\rightarrow$ `ConfigActivity (ajustes)` [cite: 32] |

### II. Intents Implícitos (5/5)
[cite_start]Estos Intents solicitan al sistema operativo (Android) que abra funcionalidades o aplicaciones externas[cite: 25].

| No. | Intent | Descripción / Acción | Pasos de Prueba |
| :---: | :--- | :--- | :--- |
| 1 | **Abrir Ubicación (Mapa)** | [cite_start]Usa `geo:q=` para mostrar una dirección fija en Google Maps[cite: 22]. | Clic en 'Abrir Mapa'. **Resultado Esperado:** Abre Google Maps centrado en la dirección de Vergara 165. |
| 2 | **Ver Página Web** | [cite_start]Usa `ACTION_VIEW` para abrir la URL configurada en el navegador[cite: 22]. | Clic en 'Abrir sitio web'. **Resultado Esperado:** Abre la URL guardada en Configuración (o la predeterminada). |
| 3 | **Llamar (DIAL)** | [cite_start]Usa `ACTION_DIAL` para abrir el marcador con el número prellenado[cite: 22]. | Clic en 'Abrir Marcador Telefónico'. **Resultado Esperado:** Abre la app de teléfono con el número (+569...) listo para llamar. |
| 4 | **Enviar Correo** | [cite_start]Usa `ACTION_SENDTO` con `mailto:` para abrir un chooser de apps de correo[cite: 22]. | Clic en 'Enviar correo'. **Resultado Esperado:** Muestra un selector de apps de correo con el destinatario prellenado con el email del usuario. |
| 5 | **Tomar Fotografía** | [cite_start]Usa `MediaStore.ACTION_IMAGE_CAPTURE` para abrir la cámara y guarda la foto en la Galería[cite: 22]. | Clic en 'Abrir Cámara' $\rightarrow$ Tomar Foto $\rightarrow$ Clic en 'Guardar Foto'. **Resultado Esperado:** Se guarda en la Galería y se redirige a la imagen. |

## 🛡️ Validaciones y Código Clave

[cite_start]Se han implementado las siguientes validaciones y buenas prácticas (Criterio 2 y 3)[cite: 17]:

* **Validaciones de Login:** Uso de `Patterns.EMAIL_ADDRESS` para el formato de correo y validación de longitud mínima de 6 caracteres para la contraseña.
* **Permisos en Tiempo de Ejecución:** Uso de `ActivityResultLauncher` para gestionar el permiso de **Cámara** y el permiso de **Almacenamiento** (`WRITE_EXTERNAL_STORAGE` para API $\le 28$).
* **Persistencia Segura:** Uso de `SharedPreferences` (claves centralizadas en `Prefs.java`) para guardar la URL y el teléfono, y para guardar el nombre del usuario.
* **Guardado Moderno:** La función de la cámara utiliza la **`MediaStore` API** para guardar la foto en una ubicación pública (`/Pictures/MiApp`), garantizando la compatibilidad con Android 10+.

## 🖼️ Capturas del Proyecto (Mínimo 4)
[Insertar Captura 1: Pantalla de Login con diseño moderno]
[Insertar Captura 2: Pantalla Home con el nuevo diseño de tarjetas/grid]
[Insertar Captura 3: Pantalla Perfil mostrando el email y campo para el nombre]
[Insertar Captura 4: Resultado de la captura de foto (ImageView o la Galería)]

## 📦 Entrega y Compilación
* **Enlace al Repositorio:** [**COLOCAR ENLACE DE GITHUB AQUÍ**]
* **Rama de Trabajo:** `feature/intents`
* [cite_start]**Commits:** Commits atómicos y descriptivos[cite: 37].
* [cite_start]**APK Debug:** [Subir el archivo `app/build/outputs/apk/debug/app-debug.apk` aquí o incluir instrucciones para compilar en Android Studio][cite: 42].
