# Reminder Book 📱

Reminder Book es una aplicación nativa de Android diseñada para la gestión eficiente de tareas diarias y recordatorios cíclicos. El proyecto fue construido siguiendo los estándares de desarrollo más modernos de la industria, priorizando la separación de responsabilidades y la optimización de procesos en segundo plano.

## 🛠️ Stack Tecnológico & Arquitectura

* **Arquitectura Modular (Now in Android):** Estructura basada en módulos independientes de características (`features`) y componentes núcleo (`core`), lo que garantiza un bajo acoplamiento y escalabilidad.
* **Jetpack Compose:** Interfaz de usuario 100% declarativa con animaciones reactivas mediante `AnimatedVisibility` y soporte nativo para Modo Oscuro.
* **Agendamiento Preciso (`AlarmManager`):** Implementación de un motor de alertas que utiliza `setExactAndAllowWhileIdle` para garantizar la ejecución de notificaciones respetando las restricciones de batería del sistema (Doze Mode).
* **Navigation Component:** Gestión avanzada del Backstack mediante navegación modular y manejo dinámico de argumentos con limpieza inclusiva de rutas (`popUpTo`).

## 🧠 Características Principales

* **Lógica Cíclica Avanzada:** Algoritmo personalizado que discrimina entre tareas de una sola vez y recordatorios semanales repetitivos basándose en mapeo de índices binarios.
* **IPC Seguro:** Comunicación entre procesos a través de `BroadcastReceiver` con `PendingIntent` configurados bajo políticas modernas de seguridad (`FLAG_IMMUTABLE`).
* **UI/UX Fluida:** Flujos de estados visuales para tareas pendientes, completadas y bloqueadas con microinteracciones de interfaz.

## 📁 Estructura del Código Clave

La lógica del agendamiento de alarmas y cálculo de tiempos se encuentra centralizada en el componente `AlarmScheduler`, optimizando el uso de la API de `Calendar` de Android para evitar wakelocks innecesarios.

<img width="1344" height="2992" alt="Screenshot_20260527_122422" src="https://github.com/user-attachments/assets/a7051f1b-b335-4236-8738-e38ee68b56cb" />
<img width="1344" height="2992" alt="Screenshot_20260527_122405" src="https://github.com/user-attachments/assets/cb58e630-3f10-4bb3-acbd-c33ec72a8c32" />
<img width="1344" height="2992" alt="Screenshot_20260527_122359" src="https://github.com/user-attachments/assets/e9663ecc-a2e3-42a7-892c-a8880d8d5042" />
<img width="1344" height="2992" alt="Screenshot_20260527_122349" src="https://github.com/user-attachments/assets/9f43ed71-aa24-4d10-9f48-35eafdf5e32c" />
<img width="1344" height="2992" alt="Screenshot_20260527_122342" src="https://github.com/user-attachments/assets/8be2166f-197e-4670-8098-a8ddcbffd42f" />
<img width="1344" height="2992" alt="Screenshot_20260527_122330" src="https://github.com/user-attachments/assets/fc6e88c8-4528-4323-9d23-28c4225ce7f2" />
<img width="1344" height="2992" alt="Screenshot_20260527_121943" src="https://github.com/user-attachments/assets/35672dfd-5bcb-4537-a6d5-a9d40355f3a8" />
