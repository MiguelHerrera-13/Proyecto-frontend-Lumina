# Plan Integral de 60 Issues de Frontend (Issue Frontend #8 a #67) - Lúmina Android

Este documento contiene la especificación completa, formateada y estandarizada de las **60 nuevas issues** para la aplicación Android de Lúmina, continuando a partir de la última issue registrada (**Issue Frontend #7**).

---

## Índice de Módulos

1. [Módulo 1: Autenticación, Seguridad y Gestión de Sesión (Frontend #8 a #13)](#módulo-1-autenticación-seguridad-y-gestión-de-sesión)
2. [Módulo 2: Gestión de Pacientes y Ficha Médica Móvil (Frontend #14 a #19)](#módulo-2-gestión-de-pacientes-y-ficha-médica-móvil)
3. [Módulo 3: Turnos, Calendario y Agenda (Frontend #20 a #25)](#módulo-3-turnos-calendario-y-agenda)
4. [Módulo 4: Reportes Diarios y Bitácora de Cuidados (Frontend #26 a #31)](#módulo-4-reportes-diarios-y-bitácora-de-cuidados)
5. [Módulo 5: Medicamentos y Prescripciones (Frontend #32 a #37)](#módulo-5-medicamentos-y-prescripciones)
6. [Módulo 6: Panel de Alertas y Notificaciones (Frontend #38 a #43)](#módulo-6-panel-de-alertas-y-notificaciones)
7. [Módulo 7: Historial Clínico, Documentos y Adjuntos (Frontend #44 a #49)](#módulo-7-historial-clínico-documentos-y-adjuntos)
8. [Módulo 8: Experiencia Offline y Persistencia Local (Frontend #50 a #55)](#módulo-8-experiencia-offline-y-persistencia-local)
9. [Módulo 9: Diseño UI/UX, Accesibilidad y Microinteracciones (Frontend #56 a #61)](#módulo-9-diseño-uiux-accesibilidad-y-microinteracciones)
10. [Módulo 10: Calidad, Testing y Arquitectura (Frontend #62 a #67)](#módulo-10-calidad-testing-y-arquitectura)

---

## Módulo 1: Autenticación, Seguridad y Gestión de Sesión

### Issue Frontend #8: Flujo de Login Real con Almacenamiento Seguro de JWT en DataStore
**Descripción:** Conectar la pantalla de inicio de sesión con el endpoint `POST /api/auth/login` del backend, persistiendo el Access Token y Refresh Token de forma cifrada mediante Jetpack DataStore / EncryptedSharedPreferences.
**Criterios de Aceptación:**
- [ ] Conectar `LoginViewModel` con el servicio Retrofit de autenticación.
- [ ] Guardar token JWT y rol del usuario en DataStore de preferencias.
- [ ] Redireccionar automáticamente al Dashboard correspondiente según el rol (`ADMIN`, `MEDICO`, `CUIDADOR`).
- [ ] Mostrar indicador de carga y manejar errores con Snackbar rojo ante credenciales inválidas (401).
**Preguntas Académicas (PR):**
1. ¿Por qué es recomendable utilizar Jetpack DataStore en lugar de las tradicionales SharedPreferences en aplicaciones Android modernas?
2. ¿Cómo desacoplaste el almacenamiento de credenciales de la capa visual en Compose?
**Fuera de Alcance:** Recuperación de contraseña y autenticación biométrica (se abordarán en tareas posteriores).

### Issue Frontend #9: Interceptor Retrofit con Auto-Refresco de Token (Authenticator)
**Descripción:** Implementar un interceptor de OkHttp que añada automáticamente el header `Authorization: Bearer <token>` a cada solicitud y gestione la renovación transparente con el endpoint de refresh cuando el token expire (401).
**Criterios de Aceptación:**
- [ ] Crear `AuthInterceptor` que inyecte el token guardado en cada request.
- [ ] Crear `TokenAuthenticator` que capture respuestas 401, invoque `/api/auth/refresh` y reintente la llamada original.
- [ ] Si el refresh token también expira, limpiar la sesión y navegar a la pantalla de Login.
**Preguntas Académicas (PR):**
1. ¿Cuál es la diferencia entre un `Interceptor` y un `Authenticator` en la librería OkHttp?
**Fuera de Alcance:** Cierre de sesión forzado desde panel web administrativo.

### Issue Frontend #10: Pantalla de Perfil de Usuario y Datos Personales
**Descripción:** Diseñar e implementar la pantalla de perfil (`ProfileScreen`) para consultar la información del usuario autenticado (`GET /api/auth/me`) y permitir editar su nombre o teléfono.
**Criterios de Aceptación:**
- [ ] Composable `ProfileScreen` con tarjeta de cabecera (avatar, rol, email).
- [ ] Formulario editable de datos secundarios con validación.
- [ ] Botón de "Guardar Cambios" con feedback visual y llamada a `PUT /api/usuarios/{id}`.
**Preguntas Académicas (PR):**
1. ¿Cómo gestionas el estado editable de los campos de texto en Compose sin perder los datos al rotar la pantalla?
**Fuera de Alcance:** Subida de foto de perfil a servidor de archivos.

### Issue Frontend #11: Pantalla de Cambio Voluntario de Contraseña
**Descripción:** Construir la interfaz de usuario para que el usuario autenticado pueda cambiar su contraseña actual por una nueva, validando requisitos de seguridad antes de enviarla.
**Criterios de Aceptación:**
- [ ] Campos para Contraseña Actual, Nueva Contraseña y Confirmar Nueva Contraseña.
- [ ] Indicador visual dinámico de fortaleza de contraseña (débil, media, fuerte).
- [ ] Botón habilitado solo cuando las contraseñas coinciden y cumplen las reglas.
- [ ] Consumo del endpoint `PUT /api/auth/change-password`.
**Preguntas Académicas (PR):**
1. ¿Por qué es fundamental validar la igualdad de contraseñas localmente en la UI antes de llamar a la API?
**Fuera de Alcance:** Recuperación de cuenta por SMS.

### Issue Frontend #12: Pantalla de Recuperación y Reseteo de Contraseña
**Descripción:** Implementar las pantallas para solicitar código o enlace de recuperación por email (`ForgotPasswordScreen`) y la pantalla para ingresar la nueva contraseña (`ResetPasswordScreen`).
**Criterios de Aceptación:**
- [ ] Pantalla con campo de email y validación de formato antes de enviar.
- [ ] Pantalla para ingresar token de reseteo recibido y nueva contraseña.
- [ ] Feedback amigable sin filtrar si el correo existe o no en el sistema.
**Preguntas Académicas (PR):**
1. ¿Qué patrón de navegación usaste para guiar al usuario desde el Login hacia el flujo de reseteo y volver?
**Fuera de Alcance:** Envío real de correos desde la app móvil (lo gestiona el backend).

### Issue Frontend #13: Autenticación Biométrica (Huella y Reconocimiento Facial)
**Descripción:** Integrar la API `androidx.biometric` para permitir el inicio de sesión rápido en dispositivos con sensores biométricos activados.
**Criterios de Aceptación:**
- [ ] Comprobar disponibilidad de hardware biométrico con `BiometricManager`.
- [ ] Desplegar `BiometricPrompt` al abrir la app si hay una sesión guardada previamente.
- [ ] Opción en ajustes de la app para activar o desactivar el desbloqueo biométrico.
**Preguntas Académicas (PR):**
1. ¿Cómo se complementa la autenticación biométrica de Android con los tokens JWT guardados localmente?
**Fuera de Alcance:** Almacenamiento de claves criptográficas en Hardware Keystore seguro (fase avanzada).

---

## Módulo 2: Gestión de Pacientes y Ficha Médica Móvil

### Issue Frontend #14: Búsqueda Reactiva y Filtros en Lista de Pacientes
**Descripción:** Mejorar la pantalla de lista de pacientes agregando una barra de búsqueda (`SearchBar` de Material 3) con debounce y filtros por estado (activos/inactivos).
**Criterios de Aceptación:**
- [ ] Campo de búsqueda superior que filtre por nombre, apellido o DNI.
- [ ] Implementar operador de debounce (300-500ms) en Kotlin Coroutines/Flow para evitar llamadas de red excesivas.
- [ ] Chips filtrables de Material 3 para alternar entre "Todos", "Activos" e "Inactivos".
**Preguntas Académicas (PR):**
1. ¿Qué problema resuelve el operador `debounce` en un flujo de búsqueda conectado a una API REST?
**Fuera de Alcance:** Paginación infinita (se aborda en la issue #15).

### Issue Frontend #15: Paginación Infinita en Lista de Pacientes con Android Paging 3
**Descripción:** Integrar la biblioteca oficial Android Paging 3 con Jetpack Compose para cargar los pacientes de forma progresiva a medida que el usuario hace scroll vertical.
**Criterios de Aceptación:**
- [ ] Crear `PatientPagingSource` que consuma el endpoint paginado del backend (`page`, `size`).
- [ ] Usar `collectAsLazyPagingItems()` en el composable de la lista.
- [ ] Mostrar ítem de carga al final de la lista mientras se descarga la siguiente página.
**Preguntas Académicas (PR):**
1. ¿Cuáles son las ventajas de usar Android Paging 3 frente a gestionar la paginación manualmente con variables `page` en el ViewModel?
**Fuera de Alcance:** Caché persistente en base de datos local Room (se implementará en el módulo offline).

### Issue Frontend #16: Ficha Médica Detallada del Paciente con Pestañas
**Descripción:** Crear la pantalla de detalle integral del paciente (`PatientDetailScreen`) organizada en pestañas (General, Turnos, Medicamentos, Historial) con TabRow de Material 3.
**Criterios de Aceptación:**
- [ ] Cabecera con datos clave del paciente (foto/iniciales, edad, DNI, médico asignado).
- [ ] `PrimaryTabRow` para navegar fluidamente entre las secciones del paciente.
- [ ] Carga asíncrona de datos con estados `Loading`, `Success` y `Error`.
**Preguntas Académicas (PR):**
1. ¿Cómo estructuraste el estado de la pantalla (`UiState`) para coordinar la información de múltiples pestañas?
**Fuera de Alcance:** Edición de ficha médica (issue #17).

### Issue Frontend #17: Formulario de Registro y Edición de Pacientes
**Descripción:** Construir el formulario para dar de alta a un nuevo paciente o editar sus datos existentes, con validaciones en vivo para DNI, fecha de nacimiento y teléfono.
**Criterios de Aceptación:**
- [ ] Formulario con campos: Nombre, Apellido, DNI, Fecha de Nacimiento, Dirección, Teléfono.
- [ ] DatePicker modal para seleccionar la fecha de nacimiento.
- [ ] Validación de campos obligatorios en el ViewModel habilitando el botón de guardado.
- [ ] Consumo de `POST /api/pacientes` o `PUT /api/pacientes/{id}` según corresponda.
**Preguntas Académicas (PR):**
1. ¿Cómo manejas las diferencias entre el modo "Creación" y el modo "Edición" en un mismo composable?
**Fuera de Alcance:** Asignación de cuidadores.

### Issue Frontend #18: Registro y Visualización Gráfica de Signos Vitales
**Descripción:** Crear la interfaz para registrar signos vitales (presión, frecuencia cardíaca, temperatura, glucosa) y mostrar gráficos de evolución temporal.
**Criterios de Aceptación:**
- [ ] Diálogo o pantalla de carga rápida de signos vitales con validación de rangos biológicos.
- [ ] Gráfico de líneas (usando Canvas de Compose o biblioteca de gráficos) mostrando la evolución histórica.
- [ ] Resaltado visual en color rojo si el valor ingresado excede los umbrales seguros.
**Preguntas Académicas (PR):**
1. ¿Qué enfoque utilizaste para renderizar gráficos fluidos y responsivos en Jetpack Compose?
**Fuera de Alcance:** Generación de alarmas sonoras en el dispositivo móvil.

### Issue Frontend #19: Gestión de Contactos de Emergencia con Marcado Rápido
**Descripción:** Implementar la tarjeta de contactos de emergencia del paciente con botón de llamada telefónica directa mediante Intent de Android (`ACTION_DIAL`).
**Criterios de Aceptación:**
- [ ] Lista de contactos con nombre, parentesco y número telefónico.
- [ ] Botón de llamada que lance el marcador del teléfono con el número precargado.
- [ ] Formulario modal para agregar o editar un contacto de emergencia.
**Preguntas Académicas (PR):**
1. ¿Por qué es más recomendable usar `Intent.ACTION_DIAL` en lugar de `Intent.ACTION_CALL` para contactos de emergencia?
**Fuera de Alcance:** Envío automático de mensajes SMS sin intervención del usuario.

---

## Módulo 3: Turnos, Calendario y Agenda

### Issue Frontend #20: Calendario Interactivo de Turnos en Jetpack Compose
**Descripción:** Desarrollar un componente de calendario semanal/mensual interactivo para visualizar los turnos del día seleccionado tanto para cuidadores como para pacientes.
**Criterios de Aceptación:**
- [ ] Fila horizontal con selector de días de la semana y visualización del mes actual.
- [ ] Indicador con punto de color bajo los días que tienen turnos programados.
- [ ] Al seleccionar un día, actualizar la lista de turnos asociados en pantalla.
**Preguntas Académicas (PR):**
1. ¿Cómo optimizaste el renderizado del calendario para evitar recomposiciones innecesarias al cambiar de fecha?
**Fuera de Alcance:** Sincronización bidireccional con Google Calendar.

### Issue Frontend #21: Pantalla de Solicitud y Creación de Turnos Médicos
**Descripción:** Construir la interfaz de usuario para solicitar o agendar un nuevo turno, permitiendo seleccionar paciente, médico, fecha y rango horario.
**Criterios de Aceptación:**
- [ ] Selectores integrados de Fecha (`DatePicker`) y Hora (`TimePicker`) de Material 3.
- [ ] Dropdowns para elegir paciente y profesional asignado.
- [ ] Validación de que la hora de inicio sea anterior a la de finalización.
- [ ] Envío a `POST /api/turnos` con manejo de errores de solapamiento (409 Conflict).
**Preguntas Académicas (PR):**
1. ¿Cómo presentas un error de colisión de agenda (409) de manera comprensible para el usuario en la UI?
**Fuera de Alcance:** Creación masiva de turnos recurrentes.

### Issue Frontend #22: Pantalla de Detalle de Turno y Acciones de Estado
**Descripción:** Crear la pantalla detallada de un turno (`TurnoDetailScreen`) con información del paciente, médico, horario y botones de acción según el estado del turno.
**Criterios de Aceptación:**
- [ ] Badge de estado con color distintivo (`PENDIENTE`, `EN_CURSO`, `COMPLETADO`, `CANCELADO`).
- [ ] Botón "Iniciar Turno" visible si el turno está pendiente y en horario.
- [ ] Botón "Finalizar y Crear Reporte" que redirija al formulario de reporte diario.
**Preguntas Académicas (PR):**
1. ¿Cómo limitas la visibilidad de las acciones de un turno según el rol del usuario conectado en Compose?
**Fuera de Alcance:** Firma digital del turno.

### Issue Frontend #23: Diálogo Modal de Cancelación y Reprogramación de Turnos
**Descripción:** Diseñar un diálogo modal de confirmación para cancelar o reprogramar un turno, exigiendo un motivo obligatorio.
**Criterios de Aceptación:**
- [ ] Campo de texto para justificar la cancelación o reprogramación (mínimo 10 caracteres).
- [ ] En reprogramación, selectores de nueva fecha y hora.
- [ ] Conexión a los endpoints `POST /api/turnos/{id}/cancelar` y `reprogramar`.
**Preguntas Académicas (PR):**
1. ¿Cómo desacoplaste la lógica de validación del diálogo de la vista principal del turno?
**Fuera de Alcance:** Cobro o penalizaciones monetarias por cancelación tardía.

### Issue Frontend #24: Filtros Avanzados de Turnos con Modal Bottom Sheet
**Descripción:** Implementar un panel inferior deslizable (`ModalBottomSheet`) para aplicar filtros avanzados a la lista de turnos (rango de fechas, médico, estado).
**Criterios de Aceptación:**
- [ ] Botón en la barra superior que despliegue el `ModalBottomSheet`.
- [ ] Controles para filtrar por estado múltiple, profesional y fechas.
- [ ] Botones "Limpiar Filtros" y "Aplicar" con recarga reactiva de la lista.
**Preguntas Académicas (PR):**
1. ¿Qué ventajas en UX ofrece un `ModalBottomSheet` frente a navegar a una pantalla completa de filtros?
**Fuera de Alcance:** Guardado de filtros favoritos en preferencias.

### Issue Frontend #25: Exportación de Turnos al Calendario Nativo de Android
**Descripción:** Permitir al usuario añadir un turno confirmado a la app de calendario del dispositivo Android mediante la API de `CalendarContract`.
**Criterios de Aceptación:**
- [ ] Botón "Añadir a mi calendario" en la vista de detalle del turno.
- [ ] Lanzar Intent con `CalendarContract.Events.CONTENT_URI` precargando título, fechas y descripción.
- [ ] Manejo de permisos correspondientes en Android si aplica.
**Preguntas Académicas (PR):**
1. ¿Por qué es más conveniente lanzar un Intent de calendario en lugar de escribir directamente en el ContentProvider de calendario?
**Fuera de Alcance:** Notificaciones de alarma creadas por la propia app de Lúmina.

---

## Módulo 4: Reportes Diarios y Bitácora de Cuidados

### Issue Frontend #26: Formulario de Creación de Reporte Diario con Stepper
**Descripción:** Implementar el formulario para que el cuidador elabore el reporte diario del turno, dividido en pasos o secciones colapsables (General, Ingesta, Higiene, Incidentes).
**Criterios de Aceptación:**
- [ ] Interfaz estructurada que evite sobrecargar la pantalla con campos continuos.
- [ ] Indicador de progreso de llenado del reporte.
- [ ] Guardado de borrador temporal en memoria ante salidas accidentales.
**Preguntas Académicas (PR):**
1. ¿Cómo gestionas el estado complejo de un formulario de múltiples secciones en un único ViewModel?
**Fuera de Alcance:** Cierre definitivo del reporte.

### Issue Frontend #27: Módulo Visual de Registro de Ingesta y Control de Hidratación
**Descripción:** Diseñar un componente interactivo para asentar las comidas consumidas (porcentaje) y la cantidad de agua ingerida en mililitros.
**Criterios de Aceptación:**
- [ ] Selector visual de porcentaje (0%, 25%, 50%, 75%, 100%) para el almuerzo/cena.
- [ ] Contador rápido de vasos de agua (250 ml c/u) con total acumulado automático.
- [ ] Botón para agregar notas especiales sobre apetito o dificultad de deglución.
**Preguntas Académicas (PR):**
1. ¿Qué elementos de diseño aplicaste para que el registro de ingesta sea rápido para un cuidador en guardia?
**Fuera de Alcance:** Cálculo automático de calorías o tablas nutricionales complejas.

### Issue Frontend #28: Registro de Control de Higiene, Deposiciones y Micción
**Descripción:** Crear los selectores rápidos para asentar cambios de pañal, micción y deposiciones durante el turno del paciente.
**Criterios de Aceptación:**
- [ ] Chips o botones de selección rápida para micción (normal, escasa, ausente).
- [ ] Selector para características de deposición y anomalías detectadas.
- [ ] Campo para observaciones sobre la piel o higiene personal.
**Preguntas Académicas (PR):**
1. ¿Cómo garantizas la privacidad y discreción visual en el diseño de esta sección médica?
**Fuera de Alcance:** Fotografías de lesiones o dermatitis (módulo de adjuntos).

### Issue Frontend #29: Registro Rápido de Incidentes Clínicos y Caídas
**Descripción:** Implementar la sección de reporte urgente de incidentes (caídas, golpes, desorientación, mareos) con selector de severidad visual.
**Criterios de Aceptación:**
- [ ] Selector de severidad con código cromático (Amarillo = Leve, Naranja = Moderado, Rojo = Crítico).
- [ ] Campo de descripción del hecho y medidas inmediatas adoptadas.
- [ ] Advertencia visual recordando que los incidentes críticos notifican automáticamente al médico.
**Preguntas Académicas (PR):**
1. ¿Cómo aseguras que el usuario confirme expresamente antes de registrar un incidente de severidad crítica?
**Fuera de Alcance:** Botón de pánico con llamada al 911.

### Issue Frontend #30: Pantalla de Cierre Formal y Firma de Reporte Diario
**Descripción:** Construir la vista de revisión final del reporte diario donde el cuidador confirma el cierre inmutable del mismo.
**Criterios de Aceptación:**
- [ ] Resumen de todos los datos cargados antes del envío final.
- [ ] Botón de confirmación con diálogo de advertencia "Una vez cerrado, el reporte no podrá ser editado".
- [ ] Consumo del endpoint `PATCH /api/reportes-diarios/{id}/cerrar`.
**Preguntas Académicas (PR):**
1. ¿Cómo proteges la inmutabilidad de la vista una vez que el reporte pasa a estado cerrado?
**Fuera de Alcance:** Firma caligráfica sobre pantalla táctil.

### Issue Frontend #31: Historial Cronológico de Reportes Diarios con Tarjetas Expandibles
**Descripción:** Diseñar la vista histórica donde médicos y familiares autorizados pueden consultar los reportes diarios de un paciente con tarjetas expandibles.
**Criterios de Aceptación:**
- [ ] `LazyColumn` ordenada por fecha de forma descendente.
- [ ] Tarjeta compacta que al hacer clic se expande con animación suave mostrando los detalles completos.
- [ ] Badge destacando si hubo incidentes en ese reporte diario.
**Preguntas Académicas (PR):**
1. ¿Cómo implementaste la animación de expansión de tarjetas (`animateContentSize`) en Compose?
**Fuera de Alcance:** Exportación a PDF desde el móvil.

---

## Módulo 5: Medicamentos y Prescripciones

### Issue Frontend #32: Pantalla de Lista de Medicamentos Activos del Paciente
**Descripción:** Crear la pantalla que lista todos los medicamentos prescriptos al paciente, mostrando nombre, dosis, frecuencia y estado.
**Criterios de Aceptación:**
- [ ] Tarjetas con diseño limpio y tipografía clara indicando dosis y frecuencia.
- [ ] Separación visual entre medicamentos activos y tratamientos finalizados/pausados.
- [ ] Conexión al endpoint `GET /api/medicamentos/tratamiento/{id}`.
**Preguntas Académicas (PR):**
1. ¿Cómo manejas el estado vacío (`EmptyState`) cuando un paciente no tiene prescripciones activas?
**Fuera de Alcance:** Alta de nuevos medicamentos.

### Issue Frontend #33: Pantalla de Prescripción Médica (Alta de Medicamentos)
**Descripción:** Construir el formulario para que el médico prescriba un nuevo medicamento asociado al tratamiento de un paciente.
**Criterios de Aceptación:**
- [ ] Campos para nombre del medicamento, dosis (ej. "500 mg") y frecuencia (ej. "Cada 8 horas").
- [ ] Validaciones de campos no vacíos antes de permitir el envío.
- [ ] Envío a `POST /api/medicamentos` y actualización de la lista de tratamientos.
**Preguntas Académicas (PR):**
1. ¿Cómo restringes el acceso a esta pantalla únicamente para usuarios con rol `MEDICO`?
**Fuera de Alcance:** Vademécum farmacológico con autocompletado en línea.

### Issue Frontend #34: Pantalla de Check-in y Registro de Tomas del Día
**Descripción:** Diseñar la pantalla de administración de medicamentos donde el cuidador marca las dosis tomadas en tiempo real.
**Criterios de Aceptación:**
- [ ] Lista de tomas organizadas por horario del día (Mañana, Tarde, Noche).
- [ ] Botones de acción rápida: "Tomada" (verde), "Omitida" (gris), "Rechazada" (rojo).
- [ ] Si se marca omitida o rechazada, solicitar obligatoriamente el motivo en un diálogo.
- [ ] Envío a `POST /api/medicamentos/{id}/tomas`.
**Preguntas Académicas (PR):**
1. ¿Qué técnica utilizaste para actualizar optimísticamente el estado del botón en la UI antes de recibir la confirmación de la API?
**Fuera de Alcance:** Recordatorios por alarma push si la app está cerrada.

### Issue Frontend #35: Timeline y Cronograma de Tomas Diarias del Cuidador
**Descripción:** Crear una vista de línea temporal con todas las tomas del día de todos los pacientes a cargo del cuidador para organizar su jornada de medicación.
**Criterios de Aceptación:**
- [ ] Vista en orden cronológico ascendente con indicador visual de la hora actual.
- [ ] Tarjeta indicando paciente, medicamento, dosis y si ya fue suministrada o está pendiente.
**Preguntas Académicas (PR):**
1. ¿Cómo sincronizaste el indicador de la hora actual con el paso del tiempo en el composable?
**Fuera de Alcance:** Integración con reloj inteligente Wear OS.

### Issue Frontend #36: Control Visual de Stock de Medicamentos y Alertas de Reposición
**Descripción:** Mostrar el nivel de inventario de medicamentos de cada paciente mediante barras de progreso y colores de advertencia.
**Criterios de Aceptación:**
- [ ] Barra de progreso lineal (`LinearProgressIndicator`) mostrando porcentaje de stock restante.
- [ ] Color verde para stock óptimo, amarillo para bajo stock y rojo para agotado.
- [ ] Botón rápido para "Registrar reposición / ingreso de unidades".
**Preguntas Académicas (PR):**
1. ¿Cómo calculaste el porcentaje de stock en la capa de dominio sin contaminar el composable con lógica de negocio?
**Fuera de Alcance:** Pedido automático a farmacias externas.

### Issue Frontend #37: Historial de Modificaciones y Suspensiones de Medicamentos
**Descripción:** Vista para consultar el historial de cambios de dosis o suspensiones ordenadas por el médico para un medicamento específico.
**Criterios de Aceptación:**
- [ ] Lista histórica indicando fecha, médico autor, cambio realizado y motivo de suspensión.
- [ ] Consumo de `GET /api/medicamentos/{id}/historial`.
**Preguntas Académicas (PR):**
1. ¿Por qué es relevante mantener accesible este historial médico para el cuidador en la aplicación?
**Fuera de Alcance:** Comparador gráfico de evolución de dosis.

---

## Módulo 6: Panel de Alertas y Notificaciones

### Issue Frontend #38: Panel de Alertas Médicas con Filtros por Severidad
**Descripción:** Rediseñar la pantalla de alertas del médico permitiendo filtrar por nivel de gravedad (`CRITICA`, `ALTA`, `MEDIA`, `BAJA`) con tarjetas de atención visual inmediata.
**Criterios de Aceptación:**
- [ ] Tarjetas con bordes e íconos codificados por color según la severidad.
- [ ] Filtro superior con badges de conteo de alertas activas por categoría.
- [ ] Ordenamiento automático situando las alertas críticas al principio.
**Preguntas Académicas (PR):**
1. ¿Cómo estructuraste el diseño visual para que una alerta crítica llame la atención de inmediato sin saturar la pantalla?
**Fuera de Alcance:** Notificaciones Push en segundo plano.

### Issue Frontend #39: Diálogo Modal de Resolución y Justificación de Alertas
**Descripción:** Implementar el diálogo para que el médico tome una alerta y la marque como resuelta, documentando la intervención clínica efectuada.
**Criterios de Aceptación:**
- [ ] Campo de texto para la nota médica obligatoria (mínimo 15 caracteres).
- [ ] Botón de "Confirmar Resolución" que consuma `POST /api/alertas/{id}/resolver`.
- [ ] Desaparición o cambio visual de la alerta en la lista con animación fluida.
**Preguntas Académicas (PR):**
1. ¿Cómo previenes toques dobles accidentales en el botón de resolución para evitar llamadas HTTP duplicadas?
**Fuera de Alcance:** Reasignación de la alerta a otro colega médico.

### Issue Frontend #40: Notificaciones Push Locales para Alertas Médicas Críticas
**Descripción:** Integrar el gestor de notificaciones de Android (`NotificationManager`) para mostrar notificaciones en la barra del sistema ante alertas urgentes.
**Criterios de Aceptación:**
- [ ] Crear canales de notificación específicos (`NotificationChannel`) con prioridad alta para emergencias.
- [ ] Mostrar notificación nativa con título, mensaje y paciente involucrado.
- [ ] Al pulsar la notificación, abrir directamente la pantalla de detalle de la alerta (Deep Linking).
**Preguntas Académicas (PR):**
1. ¿Qué permisos específicos de notificación deben solicitarse a partir de Android 13 (Tiramisu / API 33)?
**Fuera de Alcance:** Servidor FCM en la nube.

### Issue Frontend #41: Actualización en Tiempo Real del Panel de Alertas con SSE
**Descripción:** Conectar el panel de alertas con el flujo de eventos Server-Sent Events (SSE) del backend para que aparezcan nuevas alertas automáticamente sin recargar la pantalla.
**Criterios de Aceptación:**
- [ ] Consumir endpoint `GET /api/alertas/stream` mediante OkHttp `EventSource`.
- [ ] Emitir nuevas alertas hacia un `SharedFlow` en el repositorio.
- [ ] Insertar la nueva alerta en la UI con animación de entrada.
**Preguntas Académicas (PR):**
1. ¿Cómo manejas las reconexiones automáticas cuando el dispositivo pierde momentáneamente la señal de internet?
**Fuera de Alcance:** WebSockets bidireccionales.

### Issue Frontend #42: Alerta Sonora y Háptica Diferenciada para Emergencias Críticas
**Descripción:** Configurar un patrón de vibración háptica y sonido de alta prioridad cuando ingrese una alerta clínica catalogada como crítica.
**Criterios de Aceptación:**
- [ ] Uso de `Vibrator` / `VibratorManager` con patrón de pulsos dobles para criticidad alta.
- [ ] Respetar la configuración de no molestar y volumen de notificaciones del dispositivo.
**Preguntas Académicas (PR):**
1. ¿Cómo equilibras la urgencia médica de una notificación con el respeto a las políticas del sistema operativo del usuario?
**Fuera de Alcance:** Modificación del volumen del sistema por encima de los ajustes del usuario.

### Issue Frontend #43: Pantalla de Estadísticas y Tiempos de Respuesta de Alertas
**Descripción:** Crear una pantalla analítica para directores médicos que muestre el volumen de alertas resueltas y los tiempos promedio de respuesta.
**Criterios de Aceptación:**
- [ ] Tarjetas con métricas clave (Total de alertas, % resueltas, tiempo promedio en minutos).
- [ ] Selector de período temporal (Últimas 24 horas, Esta semana, Este mes).
- [ ] Consumo del endpoint `GET /api/alertas/estadisticas`.
**Preguntas Académicas (PR):**
1. ¿Cómo formateas métricas temporales (ej. minutos a horas/días) de forma legible en la UI?
**Fuera de Alcance:** Exportación a hojas de cálculo Excel.

---

## Módulo 7: Historial Clínico, Documentos y Adjuntos

### Issue Frontend #44: Pantalla de Línea de Tiempo Unificada (Timeline Clínico)
**Descripción:** Desarrollar un timeline cronológico interactivo donde se visualicen todos los hitos del paciente (turnos, cambios de medicación, reportes e incidentes).
**Criterios de Aceptación:**
- [ ] Componente vertical con línea conectora e íconos distintivos por tipo de evento.
- [ ] Filtrado por tipo de evento mediante chips horizontales.
- [ ] Clic en un evento para abrir la vista detallada correspondiente.
**Preguntas Académicas (PR):**
1. ¿Cómo construiste la línea vertical conectora del timeline utilizando modificadores de dibujo (`Modifier.drawBehind`) en Compose?
**Fuera de Alcance:** Edición directa de eventos desde la línea de tiempo.

### Issue Frontend #45: Visor y Descarga de Historial Clínico en PDF
**Descripción:** Permitir al médico descargar y visualizar el PDF oficial del historial clínico del paciente dentro de la propia aplicación móvil.
**Criterios de Aceptación:**
- [ ] Botón "Descargar Historial Clínico en PDF".
- [ ] Descarga con progreso visual y apertura del archivo mediante un visor de PDF integrado o Intent del sistema.
- [ ] Guardado del archivo descargado en la carpeta de descargas del dispositivo.
**Preguntas Académicas (PR):**
1. ¿Cómo manejas el almacenamiento de archivos temporales y la compartición segura mediante `FileProvider` en Android?
**Fuera de Alcance:** Edición o anotaciones sobre el documento PDF.

### Issue Frontend #46: Captura de Estudios Médicos y Recetas con CameraX / Photo Picker
**Descripción:** Integrar el Photo Picker de Android y la cámara para capturar fotos de recetas médicas, análisis de sangre o estudios de laboratorio.
**Criterios de Aceptación:**
- [ ] Uso de `rememberLauncherForActivityResult` con `PickVisualMedia` para galería.
- [ ] Opción de tomar foto directa con la cámara del dispositivo.
- [ ] Vista previa de la imagen capturada antes de confirmar la subida.
**Preguntas Académicas (PR):**
1. ¿Por qué el nuevo Photo Picker de Android es preferible a solicitar permisos amplios de lectura de almacenamiento externo (`READ_EXTERNAL_STORAGE`)?
**Fuera de Alcance:** Reconocimiento óptico de caracteres (OCR) sobre la receta.

### Issue Frontend #47: Galería de Documentos Médicos Adjuntos con Zoom Interactivo
**Descripción:** Crear la galería de imágenes y documentos adjuntos de la ficha del paciente con capacidad de zoom y paneo táctil.
**Criterios de Aceptación:**
- [ ] Cuadrícula de miniaturas de documentos subidos.
- [ ] Visor a pantalla completa con soporte de gestos multitáctiles (pellizcar para zoom y arrastrar).
- [ ] Carga eficiente de imágenes con la biblioteca Coil.
**Preguntas Académicas (PR):**
1. ¿Cómo implementaste los gestos de zoom y transformación con `Modifier.pointerInput` en Compose?
**Fuera de Alcance:** Escaneo de documentos en múltiples páginas con detección de bordes.

### Issue Frontend #48: Indicador de Progreso de Subida de Archivos Multipart
**Descripción:** Implementar una barra de progreso que refleje el avance real de la subida del archivo al endpoint `POST /api/pacientes/{id}/documentos`.
**Criterios de Aceptación:**
- [ ] RequestBody personalizado de OkHttp que notifique los bytes transferidos.
- [ ] Barra de progreso en la UI con porcentaje actualizado y botón para cancelar.
- [ ] Manejo de reintento ante cortes de conexión.
**Preguntas Académicas (PR):**
1. ¿Cómo se intercepta el flujo de escritura de un `RequestBody` en OkHttp para emitir el porcentaje de subida?
**Fuera de Alcance:** Reanudación de subidas interrumpidas por bloques (chunked upload).

### Issue Frontend #49: Compartir Resumen Clínico por Aplicaciones Externas
**Descripción:** Permitir al médico o familiar responsable compartir un resumen médico seguro del paciente vía aplicaciones de mensajería o correo mediante el `ShareSheet` de Android.
**Criterios de Aceptación:**
- [ ] Botón de compartir en la ficha del paciente.
- [ ] Creación de un texto estructurado y limpio con la información esencial.
- [ ] Lanzamiento de `Intent.ACTION_SEND` con selector del sistema.
**Preguntas Académicas (PR):**
1. ¿Qué consideraciones de privacidad de datos médicos deben tenerse en cuenta al redactar el texto que se comparte externamente?
**Fuera de Alcance:** Generación de enlaces web públicos con caducidad.

---

## Módulo 8: Experiencia Offline y Persistencia Local

### Issue Frontend #50: Base de Datos Local con Room para Pacientes y Turnos
**Descripción:** Configurar la base de datos local Room para cachear los datos de pacientes y turnos, permitiendo consultar la información sin conexión a internet.
**Criterios de Aceptación:**
- [ ] Entidades de Room (`PatientRoomEntity`, `TurnoRoomEntity`) y DAOs con métodos de consulta e inserción.
- [ ] Migraciones de Room configuradas correctamente.
- [ ] Mapeo bidireccional entre modelos de Room, DTOs de red y entidades de dominio.
**Preguntas Académicas (PR):**
1. ¿Cuál es el rol de un DAO en la arquitectura de Room y cómo se relaciona con Kotlin Coroutines?
**Fuera de Alcance:** Sincronización en segundo plano con WorkManager (issue #51).

### Issue Frontend #51: Cola de Sincronización Offline con Android WorkManager
**Descripción:** Implementar trabajadores en segundo plano con `WorkManager` para encolar reportes diarios y tomas de medicación registradas sin conexión y sincronizarlas automáticamente cuando regrese internet.
**Criterios de Aceptación:**
- [ ] Crear `SyncWorker` con restricciones de red (`NetworkType.CONNECTED`).
- [ ] Guardar las operaciones pendientes en una tabla local de tareas diferidas.
- [ ] Reintento con backoff exponencial ante fallos transitorios del servidor.
**Preguntas Académicas (PR):**
1. ¿Por qué `WorkManager` es la herramienta adecuada para sincronizaciones que deben completarse incluso si la app se cierra o el dispositivo se reinicia?
**Fuera de Alcance:** Algoritmos complejos de resolución de conflictos multi-usuario.

### Issue Frontend #52: Indicador Visual de Estado de Red y Modo Sin Conexión
**Descripción:** Mostrar un banner superior animado cuando el dispositivo pierda la conexión a internet, informando que la app opera en modo local.
**Criterios de Aceptación:**
- [ ] Monitorear el estado de red con `ConnectivityManager.NetworkCallback`.
- [ ] Banner deslizable en la parte superior: "Modo sin conexión - Los cambios se sincronizarán automáticamente".
- [ ] Transición a verde momentáneo ("Conexión restaurada") al recuperar la red.
**Preguntas Académicas (PR):**
1. ¿Cómo expones el estado de conectividad de forma reactiva como un `StateFlow<Boolean>` accesible para todos los composables?
**Fuera de Alcance:** Modo avión forzado por software.

### Issue Frontend #53: Repositorios Single Source of Truth (SSOT) con Flow
**Descripción:** Refactorizar los repositorios de Pacientes y Turnos para seguir el patrón de fuente única de verdad, retornando flujos `Flow<Resource<T>>` que emitan datos locales primero y luego refresquen desde la API.
**Criterios de Aceptación:**
- [ ] El ViewModel solo observa el flujo emitido por la base de datos local.
- [ ] La llamada de red actualiza la base de datos local de forma transparente.
- [ ] Estados claros de `Loading`, `Success` y `Error`.
**Preguntas Académicas (PR):**
1. ¿Por qué el patrón Single Source of Truth mejora la fluidez de la interfaz y previene inconsistencias visuales?
**Fuera de Alcance:** Paginación combinada con RemoteMediator de Paging 3.

### Issue Frontend #54: Purga y Mantenimiento Periódico de Caché Local
**Descripción:** Implementar una rutina de limpieza periódica para eliminar datos huérfanos o reportes antiguos de la base de datos local y liberar espacio de almacenamiento.
**Criterios de Aceptación:**
- [ ] Eliminar registros de turnos finalizados hace más de 30 días de la caché local.
- [ ] Limpiar caché de imágenes de Coil en memoria y disco.
**Preguntas Académicas (PR):**
1. ¿Qué estrategias existen para evitar que una base de datos local de caché crezca indefinidamente en el dispositivo móvil?
**Fuera de Alcance:** Exportación de backup completo de la base de datos.

### Issue Frontend #55: Resolución Amigable de Conflictos de Datos en Sincronización
**Descripción:** Diseñar el diálogo de resolución cuando un dato editado sin conexión fue modificado previamente en el servidor por otro usuario (409 Conflict).
**Criterios de Aceptación:**
- [ ] Diálogo comparativo mostrando la versión del servidor y la versión local pendiente.
- [ ] Opciones para el usuario: "Sobrescribir con mis datos" o "Descartar y conservar versión del servidor".
**Preguntas Académicas (PR):**
1. ¿Cómo se presenta un conflicto de concurrencia a un usuario no técnico sin generar frustración?
**Fuera de Alcance:** Mezcla automática campo a campo.

---

## Módulo 9: Diseño UI/UX, Accesibilidad y Microinteracciones

### Issue Frontend #56: Soporte Integral de Modo Oscuro (Dark Theme) en Material 3
**Descripción:** Garantizar que todas las pantallas de la aplicación soporten temas Claro y Oscuro de forma coherente respetando las guías de Material Design 3.
**Criterios de Aceptación:**
- [ ] Definición de paleta de colores para `darkColorScheme` y `lightColorScheme` en `Theme.kt`.
- [ ] Asegurar contraste adecuado en textos, tarjetas e íconos sobre fondos oscuros.
- [ ] Selector manual en ajustes (Claro, Oscuro, Seguir sistema).
**Preguntas Académicas (PR):**
1. ¿Cómo garantiza Material 3 la coherencia de contraste en superficies y contenedores cromáticos?
**Fuera de Alcance:** Temas personalizados con colores dinámicos de Material You.

### Issue Frontend #57: Accesibilidad y Soporte de Lector de Pantalla TalkBack
**Descripción:** Auditar y optimizar la accesibilidad de la aplicación para personas con discapacidad visual o motriz.
**Criterios de Aceptación:**
- [ ] Agregar descripciones de contenido (`contentDescription`) significativas a todos los íconos e imágenes.
- [ ] Asegurar que el tamaño táctil de botones y elementos interactivos sea de al menos 48x48 dp.
- [ ] Compatibilidad con fuentes de texto ampliadas sin truncamiento de información.
**Preguntas Académicas (PR):**
1. ¿Por qué es fundamental testear la navegación con TalkBack en aplicaciones del sector de la salud?
**Fuera de Alcance:** Comandos de voz para navegación completa.

### Issue Frontend #58: Transiciones Suaves y Animaciones de Navegación en Compose
**Descripción:** Implementar transiciones animadas (deslizamiento horizontal, desvanecimiento) entre pantallas con `AnimatedNavHost` o Navigation Compose 2.7+.
**Criterios de Aceptación:**
- [ ] Animación de entrada por deslizamiento y salida con fade al cambiar de pantalla.
- [ ] Transición compartida (Shared Element Transition) de la tarjeta del paciente a su ficha detallada.
**Preguntas Académicas (PR):**
1. ¿De qué manera las animaciones de transición contribuyen a la orientación espacial del usuario en una aplicación móvil?
**Fuera de Alcance:** Animaciones Lottie complejas en pantalla de bienvenida.

### Issue Frontend #59: Efecto Shimmer (Skeleton Loading) en Cargas de Listas
**Descripción:** Reemplazar los indicadores circulares de carga genéricos por esqueletos animados tipo Shimmer en las listas de pacientes, turnos y alertas.
**Criterios de Aceptación:**
- [ ] Componente reutilizable `ShimmerListItem` con gradiente animado.
- [ ] Mostrar mientras el estado de la UI esté en `Loading`.
**Preguntas Académicas (PR):**
1. ¿Por qué los componentes de tipo Shimmer reducen la percepción de tiempo de espera en comparación con un spinner clásico?
**Fuera de Alcance:** Animaciones de placeholders interactivos.

### Issue Frontend #60: Pull-to-Refresh Unificado en Pantallas de Listados
**Descripción:** Incorporar el gesto de deslizar hacia abajo para actualizar (Pull to Refresh) en los paneles de Pacientes, Turnos y Alertas.
**Criterios de Aceptación:**
- [ ] Uso del modificador `pullRefresh` de Material 3.
- [ ] Indicador visual en la parte superior durante la recarga de datos de la API.
- [ ] Desactivación automática del indicador al concluir la llamada.
**Preguntas Académicas (PR):**
1. ¿Cómo se desacopla el estado del gesto de Pull to Refresh de la corrutina que realiza la llamada de red?
**Fuera de Alcance:** Recarga automática al agitar el dispositivo.

### Issue Frontend #61: Retroalimentación Háptica (Vibración) en Acciones Críticas
**Descripción:** Agregar pequeñas vibraciones táctiles de confirmación al completar acciones de relevancia (marcar alerta como resuelta, registrar toma de medicación, cerrar reporte).
**Criterios de Aceptación:**
- [ ] Integrar `LocalHapticFeedback.current` de Compose.
- [ ] Ejecutar `HapticFeedbackType.LongPress` o feedback de confirmación en botones clave.
**Preguntas Académicas (PR):**
1. ¿Qué valor aporta la retroalimentación háptica en la confianza del usuario al realizar acciones operativas críticas?
**Fuera de Alcance:** Retroalimentación de audio personalizada.

---

## Módulo 10: Calidad, Testing y Arquitectura

### Issue Frontend #62: Inyección de Dependencias con Hilt en Todo el Proyecto
**Descripción:** Estandarizar la inyección de dependencias en la app utilizando Dagger Hilt para ViewModels, Repositorios, Servicios Retrofit y base de datos Room.
**Criterios de Aceptación:**
- [ ] Anotar la aplicación con `@HiltAndroidApp` y actividades con `@AndroidEntryPoint`.
- [ ] Módulos de Hilt para proporcionar instancias de Retrofit, Room y DataStore como Singletons.
- [ ] Inyección de dependencias en ViewModels con `@HiltViewModel`.
**Preguntas Académicas (PR):**
1. ¿Cómo facilita la inyección de dependencias con Hilt la creación de pruebas unitarias con repositorios simulados (mocks)?
**Fuera de Alcance:** Configuración de múltiples sabores de inyección en tiempo de compilación.

### Issue Frontend #63: Suite de Pruebas Unitarias para ViewModels con MockK
**Descripción:** Crear pruebas unitarias automatizadas para los principales ViewModels (`DashboardPacienteViewModel`, `LoginViewModel`, `RegisterViewModel`).
**Criterios de Aceptación:**
- [ ] Pruebas unitarias con JUnit 4/5, MockK y `kotlinx-coroutines-test` (`StandardTestDispatcher`).
- [ ] Validar transiciones de estado de `UiState` ante respuestas exitosas y errores HTTP de la API.
**Preguntas Académicas (PR):**
1. ¿Por qué es necesario reemplazar el despachador principal de corrutinas (`Dispatchers.Main`) en las pruebas unitarias de ViewModels?
**Fuera de Alcance:** Pruebas instrumentadas en emulador.

### Issue Frontend #64: Pruebas de Interfaz UI con Jetpack Compose Testing
**Descripción:** Escribir pruebas de interfaz automatizadas con `createComposeRule` para validar los flujos de Login, validación de formularios y visualización de tarjetas.
**Criterios de Aceptación:**
- [ ] Verificar que el botón de inicio de sesión esté deshabilitado si el email no es válido.
- [ ] Simular clic en el botón y verificar la aparición del indicador de carga.
- [ ] Localización de nodos en el árbol semántico mediante `onNodeWithText` y `onNodeWithTag`.
**Preguntas Académicas (PR):**
1. ¿Qué es el árbol semántico en Jetpack Compose y por qué las pruebas de UI se basan en él en lugar de buscar por IDs tradicionales?
**Fuera de Alcance:** Pruebas de extremo a extremo con Appium.

### Issue Frontend #65: Pipeline de Integración Continua (CI) en GitHub Actions
**Descripción:** Configurar un flujo de trabajo automatizado en GitHub Actions para compilar el proyecto Android y ejecutar las pruebas unitarias en cada Push y PR.
**Criterios de Aceptación:**
- [ ] Archivo `.github/workflows/android-ci.yml`.
- [ ] Configuración de JDK 17/21 y ejecución de `./gradlew testDebugUnitTest`.
- [ ] Generación de reporte de tests como artefacto descargable.
**Preguntas Académicas (PR):**
1. ¿Qué ventajas otorga cachear el directorio de Gradle (`~/.gradle/caches`) en los runners de GitHub Actions?
**Fuera de Alcance:** Firma de APK de producción y subida a Google Play Store.

### Issue Frontend #66: Optimización de Código y Reducción de Tamaño con ProGuard / R8
**Descripción:** Configurar las reglas de ofuscación y reducción de código (minification) en `proguard-rules.pro` para la variante Release.
**Criterios de Aceptación:**
- [ ] Habilitar `isMinifyEnabled = true` y `isShrinkResources = true` en el build type `release`.
- [ ] Reglas `keep` para DTOs y modelos de datos parseados por Gson/Retrofit para evitar que R8 elimine sus campos.
- [ ] Reducción medible del tamaño del archivo APK final.
**Preguntas Académicas (PR):**
1. ¿Por qué las clases DTO que usan serialización JSON requieren reglas especiales de `@Keep` o ProGuard al activar R8?
**Fuera de Alcance:** Firma digital con keystore de producción.

### Issue Frontend #67: Configuración de Build Variants y Ambientes (Debug, Staging, Release)
**Descripción:** Configurar las variantes de compilación de Gradle para conectar la aplicación a diferentes entornos (servidor local de desarrollo, testing y producción).
**Criterios de Aceptación:**
- [ ] Configurar campos `buildConfigField` en `build.gradle.kts` para la URL base (`BASE_URL`).
- [ ] Diferenciar applicationIdSuffix (ej. `.debug` para desarrollo) permitiendo instalar ambas versiones en el mismo teléfono.
**Preguntas Académicas (PR):**
1. ¿Por qué es una mala práctica dejar URLs de servidores o claves de API escritas directamente en el código Kotlin?
**Fuera de Alcance:** Servidores de distribución interna tipo Firebase App Distribution.
