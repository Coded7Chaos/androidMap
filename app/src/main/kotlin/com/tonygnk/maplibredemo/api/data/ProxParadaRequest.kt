package com.tonygnk.maplibredemo.api.data

data class ProxParadaRequest(val ruta: Int, val dir: Int, val parada: Int)
data class ProxParadaItem(
    val ytiempototal_ida: String,
    val ytiempototal_vuelta: String,
    val ytmp_codigo_ba: String,
    val ytmp_latitude: String,
    val ytmp_longitude: String,
    val ytmp_velocidad_kilometros: String,
    val ytmp_direccion: Int,
    val ytmp_yestado: String,
    val ytmp_ysparada: String,
    val ytmp_ydrecorrido: String,
    val ytmp_ydistaciap: String,
    val ytmp_yprecorrido: String,
    val ytmp_yhora_aprox: String,
    val ytmp_hora_sparada: String,
    val ytdiferenciarecorrido_etr_bu: String,
    val yttiempollegadapardapfpi: String,
    val yttiempodifrecuenciastrebuses: String
)