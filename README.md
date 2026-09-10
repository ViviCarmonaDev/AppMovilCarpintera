# App de Carpintería 🪚

Aplicación que conecta clientes que necesitan trabajos de carpintería con carpinteros que ofrecen sus servicios, muestran su portafolio y reciben pedidos.

## Arquitectura: MVVM

La app se organiza en 3 capas principales, separando la UI de la lógica de negocio y del acceso a datos:

- View (Widgets/Pantallas)
│ observa estado vía Riverpod (ref.watch)


- ViewModel (Notifier/AsyncNotifier de Riverpod)
│ ejecuta casos de uso / lógica de presentación


- Repository (capa de datos)
│ abstrae el origen de datos (Firestore, Storage, Functions)


- Data Sources (Firebase SDKs)

## Stack Tecnológico

| Capa | Tecnología |
|---|---|
| Frontend (Android + iOS) | Flutter + Dart |
| Gestión de estado / MVVM | Riverpod |
| Backend  | Firebase |
| Autenticación | Firebase Auth |
| Base de datos | Cloud Firestore |
| Almacenamiento de archivos | Firebase Storage (fotos de portafolio, bocetos) |
| Lógica de servidor | Cloud Functions |
| Notificaciones push | Firebase Cloud Messaging (FCM) |
| Pagos | Fuera de la app (a coordinar entre cliente y carpintero) — evaluar integración en fase futura |


## Principios clave:
- Las **Views** no llaman a Firebase directamente ni contienen lógica de negocio; solo renderizan estado y disparan acciones del ViewModel.
- Los **ViewModels** (implementados como `Notifier`/`AsyncNotifier` de Riverpod) exponen el estado de la pantalla (`loading`, `data`, `error`) y contienen la lógica de presentación.
- Los **Repositories** abstraen el acceso a datos (Firestore/Storage/Functions), de forma que el ViewModel no sabe si los datos vienen de Firebase o de otra fuente. 
- Los **Modelos** son clases de datos inmutables que representan entidades: `Usuario`, `Pedido`, `TrabajoPortafolio`, etc.

