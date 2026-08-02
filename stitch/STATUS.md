# Estado Actual de Atlas NMS (Actualizado)

Este documento resume el progreso actual del desarrollo y define el punto de control (checkpoint) para los siguientes pasos.

## Hitos Completados

1. **Estructura y Arquitectura KMP**:
   - Proyecto configurado con Kotlin Multiplatform (Android + Shared UI en Compose).
   - Base de datos estructurada con SqlDelight (`AtlasDatabase`).
   - Dependencias clave listas: Coil, Navigation Compose, Coroutines.

2. **UI y Sistema de Diseño**:
   - `DESIGN.md` validado y aplicado (Sistema *Grounded Material*, tonos oscuros sin elementos neón).
   - Componentes principales creados: `AtlasBottomNav`, `ScanFab`, `DiscoveryCard`, `GlyphSequence`.

3. **Navegación Deslizable Responsiva y Animaciones**:
   - `AtlasBottomNav` refactorizado con `horizontalScroll` para garantizar que todos los ítems (`DESCUBRIMIENTOS`, `EXPLORAR`, `WIKI & NMS HUB`, `AJUSTES`, `ANALIZAR`) se desplieguen sin cortarse.
   - Transiciones suaves de 1.5 segundos (`1500ms`) en navegación y cambio de pantallas con `AnimatedContent`.

4. **Integración de Glifos PNG en Toda la App**:
   - Integración local de los 16 glifos de portal (`glyph_0.png` a `glyph_f.png`) en `composeResources/drawable`.
   - `GlyphSequence` renderiza las imágenes PNG oficiales en tarjetas, pantalla de escaneo y detalle.

5. **Pantalla de Detalle de Descubrimientos y Ciclo de Vida (`BackHandler`)**:
   - `DiscoveryDetailScreen`: Vista completa de cada hallazgo con imagen a pantalla completa, metadatos y dirección de portal de 12 glifos en PNG.
   - `AtlasBackHandler`: Manejo de ciclo de vida del botón de retroceso en Android para regresar del detalle o subpantallas a `DISCOVERIES` sin cerrar la aplicación.

6. **WebView Online Optimizado y Limpieza UI**:
   - `AtlasWebView`: Carga optimizada de la Wiki oficial (`https://nomanssky.fandom.com/es/wiki/No_Man%27s_Sky_Wiki`) con caché del navegador nativo `LOAD_DEFAULT` y gestión estricta del ciclo de vida (`onPause()`, `onResume()`, `destroy()` vía `DisposableEffect`).
   - Rediseño del Header en `WikiScreen` para seguir exactamente el sistema de diseño oscuro de la app.

---

## Siguiente Paso Prioritario (Próxima Fase)

**Conexión Real del Pipeline de Análisis en "Analizar nueva captura" (`ScanScreen`)**:
Actualmente `ScanScreen` cuenta con la interfaz de usuario interactiva y muestras de prueba. El siguiente paso directo será:

1. **Selector de Imágenes Real (ImagePicker)**:
   - Integrar la selección de imágenes directamente desde la galería del dispositivo Android o la cámara.

2. **Ejecución del Procesador de Imagen Nativo (`NativeImageProcessor`)**:
   - Procesar la imagen seleccionada mediante el motor C nativo (escala de grises, contraste y binarización adaptativa).

3. **Motor OCR e Inteligencia Artificial (ML Kit / Gemini / DeepSeek)**:
   - Conectar la extracción de texto del OCR a la API de IA para generar automáticamente el objeto `Discovery` (tipo, sistema, galaxia, 12 glifos) y guardarlo en la base de datos SqlDelight.
