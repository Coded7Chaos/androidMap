package com.tonygnk.maplibredemo.api.data

data class Ruta(
    val rt_id_rutas_traccar: Int,
    val rt_estado_operativo: String,
    val rt_id: Int,
    val rt_nombre: String,
    val rt_color_ida: String,
    val rt_color_vuelta: String,
    val rt_imagen: String,
    val rt_tipo_ruta: String,
    val rt_descripcion_ida: String,
    val rt_descripcion_vuelta: String,
    val prd_ida: String,
    val prd_ida_final: String,
    val prd_vuelta: String,
    val prd_vuelta_final: String
)