# Proyecto: Atlas NMS
## Filosofía
Atlas NMS es una bitácora personal e inteligente para exploradores de No Man's Sky. Busca reducir la fricción entre encontrar algo en el juego y guardarlo/compartirlo.
La UI debe ser fluida, responsiva y escalable en cualquier plataforma, adoptando un diseño *Grounded Material / Glassmorphism* (colores mate oscuros para descanso visual y acentos vibrantes tipo HUD para interactividad).

## Arquitectura y Restricciones
- **Kotlin Multiplatform (KMP)** y **Compose Multiplatform** para máxima consistencia y evitar duplicidad de código.
- Los componentes UI deben vivir en el módulo `shared` (`shared/src/commonMain/kotlin/ui/...`).
- Capa de datos eficiente localmente (Room KMP o SQLDelight) sincronizada con la UI mediante `StateFlow`.
- **MVP actual:** Implementar la base UI fluida (Tema, Tarjetas, BottomNav, FAB) y conectarla a una fuente de datos de prueba *Mock* de NMS Coordinate Exchange (NMSCE) para validar ubicaciones e imágenes.
