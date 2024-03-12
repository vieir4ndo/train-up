package com.example.train_up.model

import java.util.Date

data class Ejercicio(
    var nombre: String,
    var usuario: String,
    var descripcion: String,
    var duracion: Int,
    var fechaCreacion: Date,
    var imagen: ByteArray?
)