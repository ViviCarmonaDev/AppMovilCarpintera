# Merveta — App de Carpintería

Aplicación móvil que conecta **clientes** con **carpinteros** para pedidos personalizados y exhibición de portafolios de trabajos en madera.

El cliente puede registrarse, buscar carpinteros, ver sus trabajos, hacer pedidos y dejar reseñas. El carpintero puede registrarse, gestionar los pedidos que recibe, subir fotos de sus trabajos a un portafolio y llevar un historial de su actividad.

## 🎯 Objetivo del proyecto

Crear una plataforma donde el trabajo artesanal en madera tenga visibilidad y donde el proceso de pedido entre cliente y carpintero sea claro, directo y sin intermediarios.

El proyecto está pensado como una app **Android-first**, con arquitectura **MVVM** y backend en **Firebase**, para facilitar el crecimiento futuro del código y la incorporación de nuevas funcionalidades.

## 🛠️ Stack tecnológico

| Capa | Tecnología |
|------|------------|
| **Lenguaje** | Kotlin |
| **UI** | Jetpack Compose |
| **Arquitectura** | MVVM + Repository |
| **Backend** | Firebase (Auth, Firestore, Cloud Storage) |
| **Asincronía** | Coroutines + Flow |
| **Inyección de dependencias** | Hilt (planeado) |
| **Build system** | Gradle (Kotlin DSL) |

### ¿Por qué Kotlin + Compose y no Flutter?

- Proyecto **Android-first**: el mercado objetivo usa mayoritariamente Android.
- **Integración nativa con Firebase**, sin capas intermedias.
- **MVVM con soporte oficial** a través de `ViewModel` + `StateFlow`.
- Menor tamaño de app y mejor rendimiento en dispositivos de gama baja.
- Si en el futuro se necesita iOS, se puede evaluar **Kotlin Multiplatform (KMP)** reutilizando la capa de lógica de negocio.

## 🏗️ Arquitectura: MVVM

El proyecto sigue el patrón **Model-View-ViewModel (MVVM)**, separando responsabilidades en tres capas bien definidas. La meta es que la UI no sepa de dónde vienen los datos, y que la lógica de negocio no dependa de la UI.

```
┌──────────────────────────────────────────────┐
│                   VIEW                       │
│         (Jetpack Compose - UI)               │
│   Observa el estado, dispara eventos         │
└──────────────────┬───────────────────────────┘
                   │ StateFlow / eventos
                   ▼
┌──────────────────────────────────────────────┐
│                VIEWMODEL                     │
│   Expone estado de UI (StateFlow)            │
│   Ejecuta lógica de presentación             │
└──────────────────┬───────────────────────────┘
                   │ llamadas a repositorio
                   ▼
┌──────────────────────────────────────────────┐
│              REPOSITORY                      │
│   Fuente única de verdad                     │
│   Decide de dónde vienen los datos           │
└──────────────────┬───────────────────────────┘
                   │
        ┌──────────┴──────────┐
        ▼                     ▼
┌───────────────┐     ┌───────────────┐
│   Firebase    │     │  Cache local  │
│ Firestore/Auth│     │  (futuro)     │
│   Storage     │     │               │
└───────────────┘     └───────────────┘
```

### Las tres capas

**View (Compose)**: 
Es la interfaz de usuario. Solo se encarga de **mostrar** lo que el ViewModel le dice y de **enviar eventos** (clicks, texto ingresado, etc.). No tiene lógica de negocio ni accede directamente a Firebase.

**ViewModel**: 
Contiene el **estado de la pantalla** y la **lógica de presentación**. Se comunica con los repositorios, transforma los datos y los expone a la View mediante `StateFlow`. Sobrevive a cambios de configuración (rotación de pantalla, etc.).

**Model / Repository**: 
El repositorio es la **fuente única de verdad** para los datos. La View no sabe si los datos vienen de Firestore, de una caché local o de una API futura — solo pide datos al repositorio y este decide de dónde obtenerlos.

### Beneficios de este patrón en este proyecto

- **Testeabilidad**: cada capa se puede probar por separado.
- **Mantenibilidad**: si cambia Firebase por otro backend, solo se toca la capa de datos.
- **Escalabilidad**: agregar nuevas pantallas no implica reescribir la lógica.
- **Separación clara**: UI, lógica de presentación y datos nunca se mezclan.

---

## 📌 Notas de desarrollo

- Las **reglas de seguridad de Firestore** se definen por rol: cada usuario solo accede a los datos que le corresponden.
- La **subida de imágenes** al portafolio se hace con Firebase Cloud Storage, con permisos ligados al UID del usuario autenticado.
- Los **estados de un pedido** se manejan como un campo enumerado en Firestore.
- Se usará **Hilt** para inyección de dependencias cuando el esqueleto MVVM esté estable.

---

## 📄 Licencia

Proyecto personal en desarrollo. Todos los derechos reservados.
