# Estado Actual de Atlas NMS (Actualizado)

Este documento resume el progreso actual del desarrollo y define el punto de control (checkpoint) consolidado acorde al plan maestro ([plan.md](file:///c:/discolocal/PROYECTOS/COMPOSE/AtlasNMS/stitch/plan.md)).

---

## 🎯 Hitos Completados (Implementados y Verificados)

1. **Estructura y Arquitectura KMP**:
   - Proyecto Kotlin Multiplatform (Android + Shared UI Compose).
   - Base de datos estructurada con SqlDelight (`AtlasDatabase`).
   - Gestión reactiva de estado en UI.

2. **Sistema de Diseño (Grounded Material)**:
   - Paleta sci-fi oscura optimizada (*Obsidian Slate, Atlas Crimson, Warp Blue*).
   - Componentes creados: `DiscoveryCard`, `GlyphSequence`, `ScanFab`.

3. **Glifos PNG Oficiales**:
   - Integración local de los 16 glifos de portal (`glyph_0.png` a `glyph_f.png`) en `composeResources/drawable`.

4. **Navegación Superior Adaptativa (`AtlasTopNav`)**:
   - Barra de navegación en el encabezado superior con `statusBarsPadding()` para no solaparse con la barra de notificaciones del sistema Android.
   - Iconografía y símbolos para cada sección (`📋 Bitácora`, `📡 Radar`, `📚 Wiki`, `⚙️ Ajustes`, `📷 Analizar`).

5. **Radar de Teleports (Animación Tragaperras) & Teclado de Glifos 2x8**:
   - Animación de rotación estilo tragamonedas (`Slot Machine`) al generar teleports aleatorios.
   - Matriz de glifos interactiva organizada en 2 filas de 8 (1..8 arriba, 9..16 abajo) con tamaños simétricos y uniformes (`weight(1f).aspectRatio(1f)`).
   - **Control de Guardado Estricto**: Opción explícita de `MARCAR COMO VISITADO Y GUARDAR EN BITÁCORA` (`CONFIRMED`) o `GUARDAR COMO SEÑAL PENDIENTE` (`PENDING`).

6. **Selector de Imágenes Nativo (Galería & Cámara) en `ScanScreen`**:
   - `rememberImagePickerHandler` (`expect/actual`) con soporte nativo en Android para seleccionar capturas reales (`GetContent`) o tomar fotos con la Cámara (`TakePicturePreview`).

7. **Motor C Nativo & Servicio AI**:
   - `NativeImageProcessor` con preprocesamiento de píxeles a nivel de bits (escala de grises, contraste y umbral adaptativo en C).
   - `AiAnalyzerService` estructurado con manejo a prueba de fallos sin cierres de la app.

8. **Ajustes y Persistencia SQLite (`AppSettingsEntity`)**:
   - Tabla `AppSettingsEntity` en SqlDelight para guardar la **DeepSeek API Key**, el **Modelo** y la **Base URL** de forma permanente en la base de datos local SQLite.
   - Guardado diferido atómico (**"GUARDAR CONFIGURACIÓN EN SQLITE"**) y detección de cambios no guardados al salir o navegar.

9. **Wiki & NMS Hub Optimizado**:
   - `AtlasWebView` con caché nativa y eliminación del botón `ScanFab` en la pantalla de Wiki para dar 100% de área visual útil.

---

## 📌 Pendientes y Próximas Fases ([plan.md](file:///c:/discolocal/PROYECTOS/COMPOSE/AtlasNMS/stitch/plan.md))

De acuerdo al plan maestro del proyecto, quedan diferidas para las siguientes fases las siguientes características:

1. **Fase 2: Conexión de Reconocimiento OCR Nativo (ML Kit)**:
   - Conectar un motor OCR nativo para escanear regiones de texto específicas (nombre de planeta, clima, recursos, 12 glifos) desde la imagen capturada e inyectar el texto extraído directamente a la tubería C/IA.

2. **Fase 3: Envío Directo y Validación JSON con DeepSeek API**:
   - Habilitar el envío remoto de imágenes/texto OCR a la API de DeepSeek utilizando la API Key guardada en Ajustes.
   - Validar las respuestas JSON estrictas contra esquemas de `kotlinx.serialization` (asignación automática de enumeraciones `SHIP`, `PLANET`, `FAUNA`, `MULTITOOL`).

3. **Fase 4: Biblioteca NMS Offline & Herramientas de Comparación**:
   - Catálogo offline de referencia (minerales, recetas de refinería, climas, biomas, razas).
   - Herramienta comparadora de naves y planetas.

4. **Fase 5: Exportación de Fichas y Sincronización**:
   - Generación de tarjetas/fichas visuales para compartir en redes sociales.
   - Exportación de la bitácora personal a JSON/CSV y respaldo en la nube.

---

## 🟢 Estado de Compilación
- `./gradlew :androidApp:assembleDebug`: **BUILD SUCCESSFUL**.
