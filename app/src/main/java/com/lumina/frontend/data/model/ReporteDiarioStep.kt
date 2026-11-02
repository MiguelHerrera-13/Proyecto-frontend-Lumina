package com.lumina.frontend.data.model

enum class ReporteDiarioPaso(val numero: Int, val titulo: String, val descripcion: String) {
    GENERAL(1, "General", "Signos vitales y estado"),
    INGESTA(2, "Ingesta", "Alimentación e hidratación"),
    HIGIENE(3, "Higiene", "Aseo y cuidados personales"),
    INCIDENTES(4, "Incidentes", "Novedades y eventualidades")
}

data class ReporteGeneralData(
    val temperatura: String = "",
    val presionArterial: String = "",
    val estadoAnimo: String = "",
    val nivelMovilidad: String = "",
    val observacionesGenerales: String = ""
)

data class ReporteIngestaData(
    val desayunoCompletado: Boolean = false,
    val almuerzoCompletado: Boolean = false,
    val meriendaCompletada: Boolean = false,
    val cenaCompletada: Boolean = false,
    val aguaMlConsumida: Int = 0,
    val apetitoNivel: String = "",
    val observacionesIngesta: String = ""
)

data class ReporteHigieneData(
    val banoRealizado: Boolean = false,
    val cambioRopa: Boolean = false,
    val cambioPanalesVeces: Int = 0,
    val cuidadoPiel: String = "",
    val observacionesHigiene: String = ""
)

data class ReporteIncidentesData(
    val huboIncidentes: Boolean = false,
    val tipoIncidente: String = "",
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
