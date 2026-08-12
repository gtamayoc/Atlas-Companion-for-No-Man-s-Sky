# Atlas NMS: plan mejorado

**Atlas NMS** será una aplicación Android de bitácora inteligente para *No Man’s Sky*, diseñada para registrar, organizar, analizar y compartir descubrimientos con la menor intervención manual posible. Su propósito no es funcionar como un chatbot, sino como un sistema de captura estructurada: interpreta capturas de pantalla del juego, extrae información verificable y la transforma en registros útiles para el explorador. La aplicación puede aprovechar una interfaz adaptativa en Jetpack Compose, alineada con tu interés previo en Android moderno y UI adaptable.

La base conceptual debe reflejar los tipos de información que el propio juego presenta en sus menús de descubrimientos: sistemas estelares, planetas, lunas, clima, centinelas, flora, fauna, minerales, recursos y progreso de descubrimiento.  También debe ser compatible, a nivel de modelo de datos, con hallazgos que suelen compartirse en comunidades como naves, cargueros, multiherramientas, fauna, planetas y bases. [nomanssky.fandom](https://nomanssky.fandom.com/wiki/Planet_-_Discovery_Menu)

## Filosofía del producto

Atlas NMS debe reducir la fricción entre **encontrar algo interesante en el juego** y **tenerlo guardado, consultable y listo para compartir**.

El usuario no debería llenar formularios largos ni transcribir coordenadas, nombres o estadísticas manualmente. En su lugar, toma una captura desde Android, importa una imagen existente o selecciona varias capturas, y la aplicación propone un registro estructurado que el usuario solamente revisa, corrige si hace falta y guarda.

La inteligencia artificial no conversa ni improvisa información. Su función se limita a interpretar evidencia visual y textual, clasificarla dentro de categorías conocidas, detectar campos faltantes y devolver datos en un formato JSON estricto. Esto mantiene la aplicación predecible, auditable y útil como una base de datos personal.

## Objetivo principal

El objetivo central es construir una **bitácora personal de exploración de No Man’s Sky**, capaz de responder consultas concretas basadas en los propios descubrimientos del jugador:

- “¿Cuántos planetas exuberantes he encontrado?”
- “¿En qué galaxia registré mi primera nave exótica?”
- “¿Qué sistemas tienen economía de tres estrellas?”
- “¿Qué criaturas puedo adoptar como mascota?”
- “¿Qué planetas tienen clima extremo y recursos específicos?”
- “¿Dónde vi una nave parecida a esta?”
- “¿Qué hallazgos tengo pendientes de publicar o exportar?”

Además de almacenar datos, Atlas NMS debe convertir esos registros en filtros, estadísticas, favoritos, comparaciones y material exportable para una página web, una comunidad o una publicación social.

## Navegación principal

La aplicación debe usar un **menú hamburguesa** como punto de acceso a las áreas principales, acompañado por una barra inferior opcional para las acciones más frecuentes: Inicio, Capturar y Bitácora.

| Sección | Propósito |
|---|---|
| Inicio | Resumen visual de exploración, actividad reciente, estadísticas, favoritos y accesos rápidos |
| Bitácora | Línea de tiempo de todos los descubrimientos guardados, agrupada por fecha, galaxia, sistema o tipo |
| Descubrimientos | Catálogo filtrable de sistemas, planetas, fauna, naves, minerales, bases y demás entidades |
| Nuevo descubrimiento | Flujo de captura, análisis, revisión y guardado de una imagen o conjunto de imágenes |
| Favoritos | Hallazgos marcados como importantes, raros, pendientes de visitar o listos para compartir |
| Biblioteca NMS | Datos de referencia del juego: minerales, recetas, tecnologías, biomas, razas, economía, conflictos, glifos y guías |
| Herramientas | Generador de glifos, conversores, comparador de descubrimientos, calculadoras y plantillas de publicación |
| Estadísticas | Análisis de exploración personal, distribución de biomas, especies, tipos de nave y patrones encontrados |
| Exportar y compartir | Generación de fichas, imágenes, JSON, CSV y enlaces públicos o privados |
| Ajustes | Configuración de idioma OCR, plataforma, privacidad, API, respaldo, sincronización y modelos de análisis |

## Inicio

La pantalla de inicio debe funcionar como el panel de mando del explorador. No debe ser una pantalla vacía ni únicamente una lista; debe mostrar información útil al abrir la app.

Debe incluir tarjetas con:

- Número total de descubrimientos.
- Sistemas, planetas, lunas y galaxias registradas.
- Fauna descubierta, fauna adoptable y criaturas pendientes de clasificar.
- Naves, cargueros, multiherramientas y bases registradas.
- Distribución de biomas, climas, economías y conflictos.
- Últimos hallazgos añadidos.
- Descubrimientos favoritos o raros.
- Registros incompletos que requieren confirmación.
- Botón destacado: **“Analizar nueva captura”**.

La interfaz debe priorizar el contenido visual: miniaturas de capturas, etiquetas de rareza, glifos, colores de bioma y chips de filtros. Así el usuario entiende rápidamente qué ha descubierto sin navegar por formularios.

## Bitácora

La bitácora será una cronología personal de exploración. Cada entrada representa un evento: descubrir un planeta, escanear una criatura, detectar una nave, registrar un sistema, construir una base o capturar una coordenada portal.

Cada registro debe conservar:

- Fecha y hora de captura o registro.
- Imagen original y recortes relevantes.
- Tipo de descubrimiento.
- Sistema, planeta, galaxia y región, si están disponibles.
- Coordenadas galácticas y dirección mediante glifos.
- Datos extraídos por OCR e IA.
- Nivel de confianza de cada campo.
- Estado de validación: borrador, confirmado, incompleto o descartado.
- Etiquetas personalizadas.
- Notas opcionales del usuario.
- Estado de exportación o publicación.

La bitácora debe permitir vistas por lista, calendario, mapa conceptual y agrupaciones por galaxia, sistema o planeta. El usuario debe poder buscar por cualquier texto reconocido: nombre, recurso, raza, clima, color, tipo de nave o coordenada.

## Descubrimientos

La sección de descubrimientos será el inventario estructurado de Atlas NMS. En lugar de una única tabla genérica, debe dividir la información en categorías especializadas para que cada hallazgo tenga los campos correctos.

### Sistemas estelares

Cada sistema puede registrar nombre, galaxia, raza dominante, nivel económico, estado de conflicto, tipo de sistema, estación espacial, presencia de piratas, coordenadas y glifos. También debe relacionarse con todos sus planetas, naves, bases, fauna y capturas asociadas.

### Planetas y lunas

Cada planeta o luna debe incluir nombre, bioma, clima, actividad centinela, recursos, flora, fauna, minerales, agua, anillos, tipo de terreno, número de especies detectadas y estado de exploración. El menú de descubrimientos del juego ofrece precisamente datos como clima, centinelas, flora, fauna y una lista de recursos, por lo que son campos prioritarios para el modelo inicial. [nomanssky.fandom](https://nomanssky.fandom.com/wiki/Planet_-_Discovery_Menu)

### Fauna y mascotas

Los registros de fauna deben incluir especie, planeta, hábitat, comportamiento, tipo de movimiento, alimentación, horario de aparición, rareza, posibilidad de adopción y evidencia visual. La app puede mostrar filtros prácticos como “fauna voladora”, “subterránea”, “acuática”, “rara”, “depredadora” o “mascota potencial”.

Completar y subir la colección de fauna de un planeta tiene relevancia dentro del juego, por lo que Atlas NMS puede incluir un indicador de progreso por planeta: especies detectadas, especies faltantes y capturas pendientes de analizar. [nomansskyresources](https://www.nomansskyresources.com/guide-pages/making-discoveries)

### Naves y multiherramientas

Esta categoría debe permitir registrar tipo, clase, apariencia, colores, piezas visuales, estadísticas, ranuras, tecnología instalada, sistema de origen, estación o ubicación, glifos y condiciones de aparición.

El diseño debe permitir filtros similares a los que la comunidad usa al buscar naves: tipo, piezas, clase, color, frecuencia, estado y coordenadas.  Sin embargo, Atlas NMS debe diferenciar claramente entre datos detectados en una captura y datos estimados o agregados manualmente. [reddit](https://www.reddit.com/r/NMSBlackHoleSuns/comments/ejqjuy/official_launch_announcement_introducing_the_no/)

### Bases y ubicaciones

Las bases deben guardar nombre, planeta, sistema, propósito, coordenadas, glifos, imágenes, recursos cercanos y notas. Deben poder marcarse como minería, agricultura, refugio, portal, comercio, fauna especial o punto turístico.

### Minerales, flora y recursos

Cada mineral, planta o recurso puede relacionarse con el planeta donde fue visto, rareza, método de obtención, uso en recetas y evidencia capturada. La biblioteca del juego puede complementar estos datos con información de referencia, mientras que la bitácora conservará dónde y cuándo lo encontró el usuario.

## Nuevo descubrimiento

Esta es la pantalla más importante de Atlas NMS. Debe estar diseñada para completar el proceso en pocos pasos, con acciones claras y sin sobrecargar al usuario.

1. **Seleccionar origen:** tomar captura desde el dispositivo, importar imagen, pegar desde portapapeles o procesar lote de imágenes.
2. **Elegir tipo sugerido:** la app detecta automáticamente si parece una pantalla de planeta, sistema, criatura, nave, inventario, coordenadas o base; el usuario puede corregir la categoría.
3. **Extraer evidencia:** ejecutar preprocesamiento de imagen, OCR y detección visual.
4. **Interpretar estructura:** enviar únicamente el texto OCR, los elementos visuales relevantes y el contexto de categoría al motor de IA.
5. **Revisar resultado:** presentar los campos detectados con una marca de confianza: confirmado, probable, dudoso o no encontrado.
6. **Completar solo lo necesario:** permitir añadir manualmente una coordenada, una nota o una categoría cuando no sea visible.
7. **Guardar y relacionar:** asociar el registro con un sistema, planeta o hallazgo existente; si no existe, crear la entidad correspondiente.
8. **Compartir o continuar:** exportar una ficha visual, generar una publicación o analizar otra captura.

El usuario debe poder guardar un registro incompleto. Es preferible marcar campos como “no visible en la captura” o “requiere otra evidencia” antes que inventar valores.

## Captura y extracción

El procesamiento debe operar mediante una tubería clara y desacoplada:

\[
\text{Imagen} \rightarrow \text{Preprocesamiento} \rightarrow \text{OCR / visión} \rightarrow \text{Clasificación} \rightarrow \text{JSON validado} \rightarrow \text{Base de datos}
\]

### Preprocesamiento visual

Antes del OCR, la app debe mejorar la imagen mediante recorte, corrección de perspectiva, aumento de contraste, reducción de ruido, escalado y detección de zonas de interfaz. Esto es especialmente importante porque las capturas del juego pueden contener fondos complejos, tipografías pequeñas, iconos y elementos decorativos.

La aplicación debe identificar regiones como:

- Nombre de sistema, planeta o nave.
- Panel de estadísticas.
- Coordenadas y secuencias de glifos.
- Iconos de recursos o economía.
- Datos de flora, fauna y clima.
- Paneles de inventario.
- Miniaturas visuales de naves, criaturas o minerales.

## Motor de IA

DeepSeek debe actuar como un **intérprete estructurado**, no como un asistente conversacional. El prompt debe exigir que transforme la evidencia recibida a un esquema concreto y que nunca complete datos no visibles sin indicarlo.

La solicitud debe incluir:

- Categoría esperada o categorías candidatas.
- Texto producido por OCR.
- Elementos detectados por visión.
- Idioma de la interfaz del juego.
- Plataforma objetivo, inicialmente Xbox para reducir variantes.
- Catálogo de campos válidos para esa categoría.
- Valores predefinidos y sinónimos permitidos.
- Contexto de entidades existentes en la base local.
- Esquema JSON obligatorio.

La respuesta debe devolver únicamente JSON válido, sin explicaciones, markdown ni texto adicional. Cada campo debe incluir valor, fuente, confianza y estado de validación.

Ejemplo conceptual:

```json
{
  "entityType": "PLANET",
  "confidence": 0.91,
  "fields": {
    "name": {
      "value": "Elyria Prime",
      "source": "ocr",
      "confidence": 0.98
    },
    "biome": {
      "value": "LUSH",
      "source": "ocr_and_catalog_match",
      "confidence": 0.87
    },
    "weather": {
      "value": "PARADISE",
      "source": "ocr",
      "confidence": 0.82
    },
    "resources": {
      "value": ["PARAFFINIUM", "COPPER", "OXYGEN"],
      "source": "ocr",
      "confidence": 0.95
    }
  },
  "missingFields": ["portalGlyphs"],
  "warnings": []
}
```

## Normalización

La normalización es la capa que convierte el resultado variable del OCR y de la IA en datos seguros para la aplicación. Ninguna respuesta del modelo debe insertarse directamente en la base de datos.

Debe aplicar estas reglas:

- Validar el JSON contra un esquema definido por Kotlin Serialization.
- Rechazar campos desconocidos o tipos incorrectos.
- Convertir valores visuales a enumeraciones internas, por ejemplo `LUSH`, `FROZEN`, `PIRATE`, `VYKEEN` o `S_CLASS`.
- Conservar el texto OCR original para auditoría.
- Guardar valores no reconocidos como texto pendiente de revisión, no como una clasificación definitiva.
- Detectar duplicados usando nombre, coordenadas, glifos, imagen perceptual y contexto de sistema.
- Mantener historial de modificaciones para saber qué detectó la IA y qué corrigió el usuario.
- Distinguir datos extraídos, inferidos, importados y escritos manualmente.

La versión inicial debe centrarse en un único formato visual, como Xbox con idioma inglés o español configurable. Esto reduce la complejidad de UI, OCR y vocabulario antes de soportar PC, PlayStation y otras variaciones.

## Categorías configurables

Atlas NMS debe tener un sistema de categorías dinámicas. Cada categoría define qué campos puede tener, cuáles son obligatorios, cuáles se extraen automáticamente y qué catálogos se usan para validación.

Ejemplos de categorías iniciales:

- Sistema estelar.
- Planeta.
- Luna.
- Fauna.
- Flora.
- Mineral o recurso.
- Nave.
- Carguero.
- Fragata.
- Multiherramienta.
- Base.
- Portal.
- Economía.
- Conflicto.
- Raza dominante.
- Tecnología.
- Receta.
- Punto de interés.
- Coordenada o dirección de glifos.

Esto permite ampliar la app sin rediseñar toda la base de datos ni crear prompts completamente nuevos para cada tipo de hallazgo.

## Biblioteca NMS

La biblioteca debe ser la fuente de referencia interna de la aplicación. Su función es ayudar a interpretar y enriquecer datos capturados, no reemplazar los descubrimientos personales del jugador.

Debe incluir:

- Minerales, recursos y materiales.
- Recetas de fabricación y refinería.
- Tecnologías y mejoras.
- Biomas y climas.
- Tipos de economía y conflicto.
- Razas y facciones.
- Naves, clases y tipologías.
- Fauna y condiciones de aparición conocidas.
- Glifos de portal.
- Terminología del juego en español e inglés.
- Diccionario de iconos y símbolos frecuentes.

Los glifos deben tratarse como una herramienta central, ya que las comunidades de coordenadas utilizan direcciones de 12 glifos y permiten convertir entre coordenadas galácticas y direcciones de portal.  La app puede integrar un selector visual de glifos y un conversor bidireccional para copiar, validar y compartir ubicaciones. [nmsguide](https://nmsguide.com/guides/nms-portal-glyph-address-directory)

## Herramientas útiles

Atlas NMS debe incluir herramientas que resuelvan tareas concretas del explorador:

- **Generador de glifos:** crea o interpreta una dirección de portal a partir de coordenadas compatibles.
- **Constructor visual de dirección:** permite seleccionar los 12 glifos y copiar la secuencia.
- **Comparador de naves:** compara clase, tipo, colores, piezas, ranuras y estadísticas entre hallazgos.
- **Comparador de planetas:** cruza bioma, clima, centinelas, recursos, fauna y distancia.
- **Buscador de mascotas:** filtra fauna registrada por planeta, hábitat, tamaño, comportamiento o adoptabilidad.
- **Explorador de recursos:** muestra dónde has encontrado un mineral y qué recetas lo requieren.
- **Creador de publicación:** genera una ficha con imagen, nombre, sistema, galaxia, glifos, características y etiquetas.
- **Detector de duplicados:** avisa si una nueva captura parece pertenecer a un planeta, nave o sistema ya registrado.
- **Estadísticas personales:** calcula frecuencias y patrones a partir de datos propios, sin afirmar probabilidades universales no verificadas.

## UI y UX

La UI debe sentirse como una mezcla entre un atlas científico, una bitácora espacial y un catálogo visual. El objetivo es que el usuario explore su propia información rápidamente, incluso cuando tenga cientos de hallazgos.

Principios de diseño:

- Priorizar capturas, nombres, iconos y etiquetas sobre formularios extensos.
- Mostrar la confianza de los datos sin lenguaje técnico excesivo.
- Usar filtros persistentes y búsqueda global.
- Mantener siempre visible la acción de captura.
- Permitir correcciones en línea, sin abrir pantallas complejas.
- No bloquear el guardado por campos no detectados.
- Diferenciar claramente datos del jugador, datos de referencia y datos sugeridos por IA.
- Ofrecer modo oscuro como diseño principal.
- Usar color con significado: bioma, clase, rareza, estado de validación y tipo de entidad.
- Adaptarse a teléfono y tablet mediante componentes Compose responsivos.

Una tarjeta de planeta, por ejemplo, debe mostrar primero imagen, nombre, bioma, clima, recursos y glifos; la información secundaria se despliega bajo demanda. Una tarjeta de nave debe priorizar su silueta, clase, colores, piezas y coordenadas.

## Persistencia y modo offline

La app debe ser **offline-first**. El usuario puede consultar toda su bitácora, biblioteca, favoritos y hallazgos previamente analizados sin conexión.

La arquitectura de almacenamiento recomendada es:

- Room como base de datos local principal.
- DataStore para preferencias y configuración.
- Almacenamiento local de imágenes y miniaturas.
- Cola de trabajos pendientes para OCR, sincronización y análisis remoto.
- WorkManager para reintentos controlados cuando vuelva la conectividad.
- Exportación local en JSON y CSV.
- Copia de seguridad manual o sincronización opcional en la nube.

La API de DeepSeek solo se usa cuando se necesita interpretación remota y existe conexión. Si no hay internet, Atlas NMS debe permitir guardar la imagen como borrador y procesarla más tarde.

## Seguridad y privacidad

La clave de DeepSeek nunca debe estar embebida directamente en el APK. Debe administrarse mediante una configuración segura o, idealmente, a través de un backend intermedio que proteja la credencial, aplique límites de uso y registre errores sin guardar información sensible.

El usuario debe poder decidir:

- Si sus capturas se procesan localmente, remotamente o en ambos modos.
- Si los descubrimientos son privados, compartibles mediante enlace o públicos.
- Si se incluyen coordenadas y glifos al exportar.
- Si las imágenes originales se conservan, se comprimen o se eliminan tras extraer datos.
- Si se sincronizan los datos entre dispositivos.

## Exportación y comunidad

Atlas NMS debe convertir los descubrimientos en contenido reutilizable. La exportación no debe ser un añadido final, sino una capacidad nativa de cada registro.

Cada hallazgo puede exportarse como:

- Imagen tipo ficha para redes sociales.
- JSON estructurado para una web personal.
- CSV para análisis externo.
- Enlace público o privado.
- Plantilla compatible con publicaciones de comunidades.
- Código QR con coordenadas, glifos y enlace al registro.
- Colección completa de un sistema, planeta o ruta de exploración.

Esto resulta especialmente útil para naves, cargueros, multiherramientas, fauna, planetas y bases, categorías que las plataformas comunitarias de intercambio ya permiten buscar y publicar. [reddit](https://www.reddit.com/r/NMSBlackHoleSuns/comments/ejqjuy/official_launch_announcement_introducing_the_no/)

## Arquitectura propuesta

La arquitectura debe separar estrictamente interfaz, procesamiento de captura, interpretación de IA y persistencia.

```text
ui/
  home/
  journal/
  discoveries/
  capture/
  library/
  tools/
  settings/

domain/
  model/
  usecase/
  repository/

data/
  local/
  remote/
  mapper/
  export/

capture/
  image_preprocessor/
  ocr/
  visual_detector/
  classifier/

ai/
  prompt/
  schema/
  validator/
  deepseek_client/

core/
  design_system/
  navigation/
  common/
  security/
```

Las entidades de dominio pueden modelarse con Kotlin usando una base común, por ejemplo `Discovery`, y tipos especializados como `PlanetDiscovery`, `FaunaDiscovery`, `ShipDiscovery` o `BaseDiscovery`. Cada entidad debe conservar tanto sus datos normalizados como la evidencia original que justifica esos datos.

## Fases de desarrollo y Progreso Actual

### Fase 1: MVP de bitácora **[COMPLETADA]**

Construir navegación reactiva, diseño base (Grounded Material), base de datos KMP (SqlDelight), y categorías iniciales. 
- *Estado:* Implementado el `HomeScreen`, `ExploreScreen` con filtros y estadísticas, y `SettingsScreen` con normativas legales. Optimización de rendimiento para dispositivos de baja memoria integrada exitosamente.

### Fase 2: OCR y preprocesamiento **[EN PROGRESO]**

Integrar OCR local, preprocesamiento de capturas y pantalla de revisión de campos detectados.
- *Estado:* **Preprocesamiento nativo C completado** (`NativeImageProcessor` con algoritmos a nivel de bits). `ScanScreen` implementada con flujo de prueba interactivo.
- *Pendiente:* Conectar una librería OCR real (como ML Kit) y la cámara/galería del dispositivo.

### Fase 3: DeepSeek estructurado **[PENDIENTE]**

Incorporar el motor de interpretación JSON, validación de esquema, niveles de confianza, control de errores y almacenamiento de evidencia.

### Fase 4: Biblioteca y herramientas **[PENDIENTE]**

Añadir minerales, recetas, glifos, conversores, filtros avanzados, comparadores y estadísticas personales avanzadas.

### Fase 5: Exportación y sincronización **[PENDIENTE]**

Generar fichas compartibles, JSON, CSV, enlaces, copias de seguridad y una API o sitio web opcional para mostrar la colección del usuario.

## Resultado esperado

Atlas NMS no será solo una galería de capturas ni un chatbot sobre *No Man’s Sky*. Será un sistema personal de conocimiento para exploradores: toma evidencia visual del juego, la transforma en información estructurada, conserva el contexto de cada descubrimiento y permite encontrar, comparar y compartir todo lo recolectado de manera rápida y visual.