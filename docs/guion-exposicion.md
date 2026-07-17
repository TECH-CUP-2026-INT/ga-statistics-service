# Guion de Exposición — TechCUP Microservicios

**Duración total:** 20 minutos exactos  
**Formato:** Cada persona presenta 1 servicio (~1 min c/u) + temas transversales

---

## 🎬 Apertura (2 min) — Tomás Quiceno

> *"Hola, somos el equipo de TechCUP y vamos a presentar nuestra arquitectura de microservicios para la plataforma de torneos de fútbol.*
>
> *TechCUP es un sistema que permite gestionar torneos deportivos universitarios: desde registro de usuarios, inscripción de equipos, programación de partidos, estadísticas en tiempo real, notificaciones, pagos y más.*
>
> *La arquitectura está compuesta por 10 microservicios que se comunican entre sí vía REST y RabbitMQ, desplegados en Azure con un API Gateway centralizado.*
>
> *Acá tienen el diagrama de arquitectura general: (mostrar diagrama integrado)*
>
> *Cada servicio tiene su propia base de datos MongoDB, está desarrollado en Java 21 con Spring Boot, y se despliega de forma independiente en contenedores Docker."*

---

## 📦 Statistics + Seguridad (~3 min) — Juan David / Persona 1

> *"Yo voy a presentar el servicio de Estadísticas.*
>
> *(mostrar diagrama de componentes)*
>
> *Este servicio centraliza el cálculo de estadísticas de jugadores, equipos, partidos y torneos. Recibe eventos cuando termina un partido — goles, tarjetas, asistencias, minutos jugados — y a partir de eso calcula promedios, totales y rankings.*
>
> *(mostrar diagrama de clases)*
>
> *La entidad principal es PlayerMatchStat: el desempeño de un jugador en un partido. Los cálculos se hacen en memoria con streams de Java, no con consultas pesadas a MongoDB, porque el volumen de datos de un torneo universitario lo permite.*
>
> *(mostrar endpoints)*
>
> *Expone endpoints como:*
> - `GET /players/{id}/average-goals` → *promedio de goles*
> - `GET /rankings?type=GOALS` → *ranking de goleadores*
> - `GET /teams/{id}/statistics` → *estadísticas completas de un equipo*
>
> *Se conecta con el servicio de Torneos para resolver el torneo activo, y con Competencia para recibir eventos via RabbitMQ.*
>
> *(demo rápida)*
>
> *Acá podemos ver el ranking de goleadores funcionando via el API Gateway.*
>
> ---
>
> *En cuanto a seguridad, cada servicio implementa autenticación mediante JWT. El servicio de Identidad emite los tokens, y los demás servicios los validan. Las comunicaciones internas entre servicios usan una API key interna. El API Gateway centraliza la autenticación y autorización antes de que las peticiones lleguen a los microservicios."*

---

## 📦 Tournament (~2 min) — Hernán Sánchez

> *"Yo presento el servicio de Torneos.*
>
> *(mostrar diagrama de componentes)*
>
> *Este servicio maneja todo el ciclo de vida de un torneo: creación, inscripción de equipos, generación de fixture (llaves, grupos o liga), asignación de árbitros y canchas, y finalización con campeón.*
>
> *(mostrar diagrama de clases)*
>
> *Las entidades principales son Tournament, Match, Enrollment, Court. El torneo pasa por estados: ACTIVE → IN_PREPARATION → IN_PROGRESS → FINISHED.*
>
> *Consume del servicio de Pagos para validar inscripciones, y del servicio de Equipos para resolver datos de equipos.*
>
> *(mostrar endpoints clave)*
> - `POST /tournaments` → *crear torneo*
> - `POST /tournaments/{id}/enrollments` → *inscribir equipo*
> - `GET /tournaments/{id}/matchups` → *ver fixture*
> - `PATCH /tournaments/{id}/finalize` → *finalizar torneo*
>
> *Actualmente tenemos 7 torneos creados en el ambiente de pruebas, incluyendo el torneo de baloncesto, el torneo de fútbol principal, la Copa Navidad y la Copa Universitaria."*

---

## 📦 Matches + Producción (~2 min) — Johan Beltrán

> *"Yo presento el servicio de Partidos.*
>
> *(mostrar diagrama de componentes)*
>
> *Este servicio maneja el arbitraje en tiempo real: inicio del partido, cronómetro, goles, tarjetas, sustituciones, y finalización.*
>
> *(mostrar diagrama de clases)*
>
> *Las entidades principales son Match, Goal, Card, Substitution. El partido tiene estados: SCHEDULED → IN_PROGRESS → PAUSED → FINISHED.*
>
> *(mostrar endpoints)*
> - `POST /api/partidos/{id}/iniciar` → *iniciar partido*
> - `POST /api/partidos/{id}/goles` → *registrar gol*
> - `POST /api/partidos/{id}/tarjetas` → *registrar tarjeta*
> - `GET /api/partidos` → *listar partidos asignados*
>
> *Se conecta con el servicio de Competencia para obtener los partidos asignados al árbitro.*
>
> *En cuanto a los ambientes, tenemos dos: QA y Producción. En QA hacemos las pruebas de integración antes de pasar a producción. Ambos ambientes están desplegados en Azure Container Apps con sus propias bases de datos MongoDB en Atlas."*

---

## 📦 Identity (~2 min) — Persona 4

> *"Yo presento el servicio de Identidad.*
>
> *(mostrar diagrama de componentes)*
>
> *Este servicio maneja toda la autenticación: registro, login institucional, login con Google, verificación OTP, recuperación de contraseña y validación de tokens JWT.*
>
> *(mostrar diagrama de clases)*
>
> *Las entidades principales son User, OtpToken, RecoveryToken, RevokedToken, SessionActivity.*
>
> *(mostrar flujo de autenticación)*
>
> *El flujo es:*
> 1. *Usuario hace login con email/password*
> 2. *Se envía un código OTP al correo*
> 3. *Usuario valida el OTP*
> 4. *Se genera un JWT*
> 5. *Ese JWT se usa para autenticarse en los demás servicios*
>
> *(mostrar endpoints)*
> - `POST /auth/login` → *login*
> - `POST /otp/validate` → *validar OTP*
> - `POST /token/validate` → *validar JWT (consumido por otros servicios)*
>
> *Actualmente tenemos 28 usuarios registrados en el sistema."*

---

## 📦 Payment + Communication (~2.5 min) — William Ruiz

> *"Payment — maneja órdenes de pago para inscripciones a torneos, integración con PSE.*
>
> *(mostrar diagrama de componentes)*
>
> *Las entidades principales son PaymentOrder, Invoice, PseTransaction. El flujo de pago permite a los jugadores pagar su inscripción al torneo de forma segura.*
>
> *(mostrar métricas de Jira)*
>
> *En cuanto a las métricas del proyecto, en Jira gestionamos todo el desarrollo con historias de usuario organizadas por sprint. Completamos X historias, con una velocidad promedio de Y puntos por sprint. Esto nos permitió entregar los 10 microservicios en el tiempo estimado.*
>
> *Communication — maneja chats, mensajería, FAQs y tickets de soporte. Los jugadores pueden comunicarse con los organizadores del torneo a través de la plataforma."*

---

## 📦 Logistics + Notifications (~2.5 min) — Tomás Quiceno

> *"Logistics — maneja refrigerios y dotación (petos, balones, kits) para equipos y jugadores. Cada equipo puede solicitar su dotación para el torneo, y el sistema gestiona la entrega.*
>
> *(mostrar diagrama de componentes)*
>
> *Notifications — servicio de notificaciones por correo electrónico para eventos del sistema: confirmación de registro, recordatorio de partidos, resultados, cambios de horario.*
>
> *En observabilidad, todos los servicios tienen health checks en `/actuator/health`, Spring Boot Actuator expone métricas y health indicators, y tenemos logs centralizados. Esto nos permite identificar problemas a tiempo y monitorear la salud del sistema."*

---

## 📦 Teams + Users-Players + Despliegue/Demo (~3.5 min) — Jhonatan Peña

> *"Teams — gestión de equipos: creación, registro de jugadores, capitanía.*
> *Users-Players — gestión de usuarios y jugadores: registro, perfiles, tipos de usuario.*
>
> *En cuanto a requerimientos, definimos 50 requerimientos funcionales y 12 no funcionales. Cada microservicio tiene su propio conjunto de endpoints documentados con OpenAPI/Swagger.*
>
> *(mostrar despliegue)*
>
> *Todos los servicios se despliegan como contenedores Docker en Azure Container Apps, con CI/CD via GitHub Actions. Cada push a main dispara:*
> 1. *Build y tests*
> 2. *Análisis de SonarCloud*
> 3. *Construcción de imagen Docker*
> 4. *Push a Azure Container Registry*
> 5. *Deploy a Azure Container App*
>
> *El API Gateway centraliza el acceso en `https://techapi.azure-api.net`. Todos los servicios pasan por el APIM.*
>
> *(DEMO)*
>
> *Vamos a hacer una demo rápida mostrando 2-3 endpoints funcionando.*
>
> *Acá podemos ver:*
> - *El health check del servicio de Estadísticas responde 200 OK*
> - *El endpoint de ranking de goleadores devuelve datos*
> - *El API Gateway redirige correctamente las peticiones*"

---

## 📊 Métricas y Datos de Jira — William Ruiz

> *"En Jira gestionamos todo el proyecto. Completamos X sprints con las siguientes métricas:*
> - *Total de historias de usuario: X*
> - *Velocidad promedio del equipo: Y puntos por sprint*
> - *Bug rate: Z%*
> - *Cobertura de pruebas promedio: 85%+*
>
> *(mostrar dashboard de Jira)*
>
> *Esto nos permitió tener visibilidad del progreso y ajustar la planificación en cada sprint."*

---

## 🎯 Cierre (1 min) — Julián Camilo Tinjacá

> *"Para cerrar, queremos dejarles una pregunta: ¿cómo escalaría esta arquitectura si tuviéramos 100 torneos simultáneos con 10,000 usuarios cada uno?*
>
> *La respuesta está en el diseño: cada microservicio escala de forma independiente, las bases de datos MongoDB en Atlas se escalan automáticamente, y el API Gateway balancea la carga. Pero eso ya es tema de otra presentación.*
>
> *En resumen:*
> - *Tenemos 10 microservicios funcionando via API Gateway*
> - *Cada uno es independiente, con su propia base de datos y despliegue*
> - *Se comunican vía REST y RabbitMQ*
> - *El frontend consume todo desde un solo dominio: el APIM*
> - *Aprendimos un montón sobre arquitectura hexagonal, contenedores, CI/CD y trabajo en equipo*
>
> *Los esperamos mañana para la demo en vivo. ¡Gracias!*
>
> *¿Preguntas?"*

---

## 📋 Resumen de Participación

| Persona | Temas | Tiempo |
|---|---|---|
| **Tomás Quiceno** | Apertura + Logistics + Notifications + Observabilidad | ~4.5 min |
| **Juan David Rangel** | Statistics + Seguridad | ~3 min |
| **Hernán Sánchez** | Tournament | ~2 min |
| **Johan Beltrán** | Matches + Producción (QA/Prod) | ~2 min |
| **Persona 4** (asignar) | Identity | ~2 min |
| **William Ruiz** | Payment + Communication + Métricas Jira | ~2.5 min |
| **Jhonatan Peña** | Teams + Users/Players + Requerimientos/API + Despliegue/Demo | ~3.5 min |
| **Julián Camilo Tinjacá** | Cierre | ~1 min |

**Total:** ~20 min ✅
