package com.lumina.frontend.data.model

enum class TipoComida(val displayName: String) {
    DESAYUNO("Desayuno"),
    ALMUERZO("Almuerzo"),
    MERIENDA("Merienda"),
    CENA("Cena")
}

data class IngestaHidratacionData(
    val tipoComida: TipoComida = TipoComida.ALMUERZO,
    val porcentajeIngesta: Int = 100, // 0, 25, 50, 75, 100
    val vasosAgua: Int = 0,
    val notasEspeciales: String = "",
    val etiquetasRapidas: Set<String> = emptySet()
) {
    val totalMililitrosAgua: Int
        get() = vasosAgua * ML_POR_VASO

    val totalLitrosAgua: Float
        get() = totalMililitrosAgua / 1000f

    companion object {
        const val ML_POR_VASO = 250
        val PORCENTAJES_DISPONIBLES = listOf(0, 25, 50, 75, 100)
        val ETIQUETAS_COMUNES = listOf(
            "Buen apetito",
            "Poco apetito",
            "Inapetente",
            "Dificultad de deglución (Disfagia)",
            "Comió con asistencia",
            "Prefirió solo líquidos"
        )
    }
}
