
# MYLIBRARY
### Aplicación Android para gestionar una biblioteca personal

MYLIBRARY es una aplicación móvil nativa para Android que permite a cada usuario registrar y organizar los libros que ha leído.


El proyecto fue desarrollado con Kotlin en Android Studio como trabajo académico de la asignatura **Desarrollo Web y Móvil** de la **Universidad EAN**.

La aplicación incluye autenticación local, operaciones CRUD, almacenamiento persistente y navegación entre cinco pantallas.

## 1. Funcionalidades

### Autenticación
- Registro de usuarios mediante nombre, correo y contraseña.
- Validación de correo electrónico.
- Contraseña de mínimo 6 caracteres.
- Verificación de credenciales al iniciar sesión.
- Persistencia de sesión mediante SharedPreferences.
- Cierre de sesión desde la pantalla principal.
- Almacenamiento de contraseñas mediante hash PBKDF2 con salt.

### Gestión de libros (CRUD)
- Crear registros de libros leídos.
- Consultar los libros registrados en una lista.
- Visualizar el detalle de cada libro.
- Editar información de libros existentes.
- Eliminar libros con confirmación previa.
- Actualización de la lista sin reiniciar la aplicación.
- Asociación de cada libro con el usuario autenticado.

### Datos de los libros
- Título: obligatorio.
- Autor: obligatorio.
- Fecha de lectura: opcional, formato AAAA-MM-DD.
- Notas personales: opcional.

## 2. Pantallas de la aplicación

MYLIBRARY contiene cinco pantallas principales:

1. **Inicio de sesión:** permite ingresar con una cuenta registrada.
2. **Registro:** permite crear una nueva cuenta de usuario.
3. **Mis libros:** muestra la biblioteca personal mediante RecyclerView.
4. **Formulario de libro:** permite crear y editar registros.
5. **Detalle del libro:** presenta la información y los botones Volver, Editar y Eliminar.

La navegación se realiza mediante Intents y el identificador del libro seleccionado. El botón Volver permite regresar a Mis libros.

## 3. Tecnologías utilizadas

| Tecnología | Utilización |
|---|---|
| Kotlin | Lenguaje de programación |
| Android Studio | Entorno de desarrollo |
| Android SDK | Desarrollo y ejecución de la aplicación |
| XML | Diseño de las interfaces |
| Room / SQLite | Base de datos local |
| RecyclerView | Listado dinámico de libros |
| ViewModel y LiveData | Manejo de estados de autenticación |
| Repository y DAO | Acceso a los datos |
| Kotlin Coroutines y Flow | Operaciones asíncronas y actualización de listas |
| SharedPreferences | Persistencia de la sesión |
| Git y GitHub | Control de versiones y publicación del código |

## 4. Arquitectura

El proyecto organiza su código en actividades, componentes de interfaz, modelos de datos y clases de acceso a información.

Para el módulo de autenticación se utiliza una separación basada en ViewModel, Repository y DAO. En el módulo de libros, las Activities interactúan con los DAO de Room para realizar las operaciones CRUD.

### Componentes principales

- `MainActivity.kt`: inicio de sesión.
- `RegisterActivity.kt`: creación de usuarios.
- `BooksActivity.kt`: listado de libros.
- `BookFormActivity.kt`: creación y edición.
- `BookDetailActivity.kt`: consulta de detalles y eliminación.
- `BookAdapter.kt`: adaptación de datos al RecyclerView.
- `AppDatabase.kt`: configuración de Room.
- `UserDao.kt` y `BookDao.kt`: acceso a la base de datos.
- `UserEntity.kt` y `BookEntity.kt`: entidades persistentes.
- `UserRepository.kt`: operaciones sobre los usuarios.
- `LoginViewModel.kt` y `RegisterViewModel.kt`: estados de autenticación.
- `PasswordUtils.kt`: generación y verificación de hashes de contraseña.

## 5. Persistencia de datos

La aplicación utiliza una base de datos SQLite administrada mediante Room.

La base de datos se denomina `mi_biblioteca.db` y contiene las entidades de usuarios y libros.

Cada libro almacena un identificador de usuario (`userId`). Las consultas, actualizaciones y eliminaciones comprueban este identificador para trabajar con los registros del usuario autenticado.

Los datos permanecen almacenados localmente después de cerrar y volver a abrir la aplicación.

No se requiere un servidor externo para las operaciones de la aplicación.

## 6. Requisitos para ejecutar el proyecto

- Android Studio compatible con el proyecto.
- Android SDK instalado, incluida la plataforma de compilación API 37.
- Dispositivo físico Android o emulador configurado.
- Android 8.0 (API 26) o superior.
- Conexión a Internet para descargar las dependencias de Gradle cuando sea necesario.

## 7. Instalación y ejecución

1. Descargar el repositorio desde GitHub:

   https://github.com/mariayara46/MYLIBRARY

2. Seleccionar **Code > Download ZIP** o clonar el repositorio con Git.
3. Descomprimir el archivo, si se descargó como ZIP.
4. Abrir Android Studio.
5. Seleccionar **Open** y elegir la carpeta raíz `MYLIBRARY`.
6. Permitir que Android Studio sincronice Gradle y descargue las dependencias necesarias.
7. Conectar un teléfono Android con depuración USB habilitada o iniciar un emulador compatible.
8. Seleccionar el dispositivo y la configuración `app`.
9. Presionar **Run** para compilar, instalar y abrir la aplicación.

No es necesario configurar una cuenta de servicios externos ni un servidor de base de datos.

## 8. Prueba funcional recomendada

Para comprobar el funcionamiento de la aplicación:

1. Crear un usuario con correo válido y contraseña de mínimo seis caracteres.
2. Iniciar sesión con el usuario registrado.
3. Agregar un libro con título y autor.
4. Verificar que el libro aparece en Mis libros.
5. Seleccionar el libro y consultar su detalle.
6. Utilizar Volver a Mis libros.
7. Abrir nuevamente el detalle y seleccionar Editar.
8. Modificar algún campo y guardar los cambios.
9. Comprobar que la lista refleja la modificación.
10. Eliminar un libro y confirmar la operación.
11. Cerrar sesión.
12. Iniciar sesión nuevamente y comprobar la persistencia de los registros restantes.

También se pueden verificar las validaciones de campos obligatorios, contraseñas cortas y fechas incorrectas.

## 9. Navegación

El flujo principal es:

Login / Registro
|
v
Mis libros
|
+-- Agregar libro
|       |
|       +-- Guardar / Cancelar
|
+-- Detalle del libro
|       |
|       +-- Volver a Mis libros
|       +-- Editar libro
|       +-- Eliminar libro
|
+-- Cerrar sesión
|
v
Login

La navegación utiliza Intents y el back-stack de Android. El botón Volver en el detalle cierra esa pantalla y recupera la lista de libros.

## 10. Pruebas realizadas

Se realizaron pruebas funcionales en un dispositivo Android físico y se verificó la ejecución del proyecto en un emulador Android 13.

Se comprobaron los flujos de autenticación, operaciones CRUD, actualización del listado, persistencia de datos, cierre de sesión y navegación.

El proyecto también superó las comprobaciones de compilación realizadas desde Android Studio.

## 11. Alcance y limitaciones

MYLIBRARY es un prototipo académico funcional.

La autenticación y la base de datos son locales. No se incluyen sincronización en la nube, recuperación de contraseñas por correo, publicación en Google Play ni administración remota de usuarios.

El proyecto está diseñado para demostrar el uso de Kotlin, interfaces Android, Room, CRUD, navegación y control de versiones.

## 12. Repositorio

Código fuente:

https://github.com/mariayara46/MYLIBRARY


## 13. Información general

- **Institución:** Universidad EAN
- **Estudiante:**  Maria Paula Yara Ladino
- **Asignatura:** Desarrollo Web y Móvil
- **Proyecto:** MYLIBRARY
- **Tipo de entrega:** Proyecto final App Android con Login/registro y CRUD
- **Lenguaje de programación:** Kotlin
- **Entorno de desarrollo:** Android Studio

