package com.tonygnk.maplibredemo.api.data

data class ParadasAppRequest(val ruta: Int, val sentido: Int)
data class ParadaApp(
    val pro_geofence_id: Int,
    val pro_orden: Int,
    val pro_direccion: Int,
    val name_parada: String,
    val latitud: String,
    val longitud: String,
    val pro_id_sub_ruta: Int,
    val pro_sub_ruta: String
)
