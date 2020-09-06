package com.udbstudents.tseapp.models

data class Actas(
    val idMunicipio: String? = null,
    val idPartido_uno: String? = null,
    val idPartido_dos: String? = null,
    val idPartido_tres: String? = null,
    val idPartido_cuatro: String? = null,
    val votos_partUno: Int? = 0,
    val votos_partDos: Int? = 0,
    val votos_partTres: Int? = 0,
    val votos_partCuatro: Int? = 0
)