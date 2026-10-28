package com.lumina.frontend.data.model

enum class ReporteDiarioPaso(val numero: Int, val titulo: String, val descripcion: String) {
    GENERAL(1, "General", "Signos vitales y estado"),
    INGESTA(2, "Ingesta", "Alimentación e hidratación"),
    HIGIENE(3, "Higiene", "Aseo y cuidados personales"),
    INCIDENTES(4, "Incidentes", "Novedades y eventualidades")
}

data class ReporteGeneralData(
    val temperatura: String = "36.5",
    val presionArterial: String = "120/80",
    val estadoAnimo: String = "Tranquilo",
    val nivelMovilidad: String = "Autónomo",
    val observacionesGenerales: String = ""
)

data class ReporteIngestaData(
    val desayunoCompletado: Boolean = true,
    val almuerzoCompletado: Boolean = true,
    val meriendaCompletada: Boolean = false,
    val cenaCompletada: Boolean = false,
    val aguaMlConsumida: Int = 1200,
    val apetitoNivel: String = "Normal",
    val observacionesIngesta: String = ""
)

data class ReporteHigieneData(
    val banoRealizado: Boolean = true,
    val cambioRopa: Boolean = true,
    val cambioPanalesVeces: Int = 2,
    val cuidadoPiel: String = "Hidratación aplicada",
    val observacionesHigiene: String = ""
)

data class ReporteIncidentesData(
    val huboIncidentes: Boolean = false,
    val tipoIncidente: String = "Ninguno",
    val descripcionIncidente: String = "",
    val medidasTomadas: String = ""
)

data class ReporteDiarioDraft(
    val idTurno: Long,
    val idPaciente: Long = 1L,
    val general: ReporteGeneralData = ReporteGeneralData(),
    val ingesta: ReporteIngestaData = ReporteIngestaData(),
    val higiene: ReporteHigieneData = ReporteHigieneData(),
    val incidentes: ReporteIncidentesData = ReporteIncidentesData(),
    val ultimoGuardadoTimestamp: Long = System.currentTimeMillis()
)

