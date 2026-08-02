package com.gtamayoc.atlasnms.shared.ui.screens

object LegalContent {

    val PRIVACY_POLICY = """
        POLÍTICA DE PRIVACIDAD DE ATLAS NMS
        Última actualización: 2 de agosto de 2026

        1. INFORMACIÓN GENERAL
        Atlas NMS ("la Aplicación") es una bitácora inteligente de exploración para el videojuego No Man's Sky. Respetamos su privacidad y nos comprometemos a proteger sus datos personales y la información almacenada en su dispositivo.

        2. INFORMACIÓN QUE RECOPILAMOS Y SU USO
        a) Capturas de pantalla e imágenes de juego:
           La Aplicación procesa capturas de pantalla de No Man's Sky seleccionadas voluntariamente por el usuario para extraer información del juego (nombres de sistemas, planetas, fauna, naves y coordenadas de glifos).
        b) Almacenamiento Local:
           Todos los registros de descubrimientos, imágenes de caché y configuraciones personales se almacenan de manera local en el dispositivo del usuario mediante una base de datos SQLite integrada.
        c) Servicios de Visión e Inteligencia Artificial:
           Cuando se utiliza la función de análisis automático, el texto y metadatos extraídos de la captura se envían a un motor de procesamiento de lenguaje/visión (como la API de DeepSeek u OCR local) con el único fin de generar un esquema JSON estructurado. No se envían datos personales del usuario a estos servicios.

        3. PERMISOS DEL DISPOSITIVO
        - Cámara y Almacenamiento/Galería: Se utilizan exclusivamente para permitir al usuario tomar capturas o seleccionar imágenes de sus hallazgos en No Man's Sky.

        4. RETENCIÓN Y ELIMINACIÓN DE DATOS
        Dado que los datos residen principalmente en su dispositivo, usted mantiene el control total de sus registros. Puede borrar descubrimientos individuales o restablecer la base de datos completa desde el menú de Ajustes en cualquier momento. Al desinstalar la Aplicación, todos los datos almacenados localmente son eliminados permanentemente.

        5. ANÁLISIS Y TERCEROS
        No vendemos, alquilamos ni rastreamos su ubicación geográfica ni su información personal con fines publicitarios.

        6. CONTACTO
        Si tiene dudas sobre esta Política de Privacidad o el tratamiento de datos en Atlas NMS, puede comunicarse con el equipo de soporte a través del repositorio oficial del proyecto.
    """.trimIndent()

    val TERMS_AND_CONDITIONS = """
        TÉRMINOS Y CONDICIONES DE USO - ATLAS NMS
        Última actualización: 2 de agosto de 2026

        1. ACEPTACIÓN DE LOS TÉRMINOS
        Al descargar, instalar o utilizar Atlas NMS ("la Aplicación"), usted acepta cumplir con estos Términos y Condiciones. Si no está de acuerdo con alguno de los términos, no debe utilizar la Aplicación.

        2. AVISO LEGAL Y DERECHOS DE AUTOR (DESCARGO DE RESPONSABILIDAD)
        Atlas NMS es una aplicación independiente desarrollada por fans para la comunidad de exploradores. 
        - NO MAN'S SKY™ es una marca registrada de Hello Games Ltd.
        - Todos los nombres de elementos, planetas, criaturas, logotipos, imágenes y conceptos del juego No Man's Sky son propiedad intelectual exclusiva de Hello Games Ltd.
        - Atlas NMS NO está afiliada, patrocinada, respaldada ni asociada oficialmente con Hello Games Ltd.

        3. LICENCIA DE USO
        Se le otorga una licencia limitada, personal, no exclusiva, no transferible y revocable para utilizar la Aplicación exclusivamente con fines personales y no comerciales.

        4. CONTENIDO GENERADO POR EL USUARIO
        El usuario es responsable de los datos e imágenes que almacena, analiza o exporta mediante la Aplicación. El usuario garantiza que el uso de la Aplicación no viola ninguna ley local ni los términos de servicio de terceros.

        5. LIMITACIÓN DE RESPONSABILIDAD
        La Aplicación se proporciona "TAL CUAL" y "SEGÚN DISPONIBILIDAD", sin garantías de ningún tipo, explícitas o implícitas. El desarrollador no será responsable por pérdidas de datos, errores en la extracción de texto OCR o cualquier daño derivado del uso de la Aplicación.

        6. MODIFICACIONES
        Nos reservamos el derecho de modificar estos Términos en cualquier momento. El uso continuado de la Aplicación tras cualquier cambio constituye la aceptación de los nuevos términos.
    """.trimIndent()
}
