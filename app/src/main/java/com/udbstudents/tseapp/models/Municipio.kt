package com.udbstudents.tseapp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Municipio(
    val idMunicipio: String? = null,
    val idDepartamento: String? = null,
    val nombre: String? = null
)