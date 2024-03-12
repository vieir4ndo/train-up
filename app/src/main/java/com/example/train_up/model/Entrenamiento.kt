package com.example.train_up.model

import java.util.Date

data class Entrenamiento(
    var nombre: String,
    var usuario: String,
    var descripcion: String,
    var duracion: Int?,
    var fechaCreacion: Date?,
    var publico: Boolean?,
    var imagen: ByteArray
){
    var ejercicios: List<Ejercicio>? = null
    var datosUsuario: Usuario? = null
}